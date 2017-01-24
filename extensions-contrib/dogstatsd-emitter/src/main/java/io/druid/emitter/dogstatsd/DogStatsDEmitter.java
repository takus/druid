package io.druid.emitter.dogstatsd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.metamx.common.logger.Logger;
import com.metamx.emitter.core.Emitter;
import com.metamx.emitter.core.Event;
import com.metamx.emitter.service.ServiceMetricEvent;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.StatsDClientErrorHandler;

import java.io.IOException;
import java.util.Map;

/**
 */
public class DogStatsDEmitter implements Emitter
{

  private final static Logger log = new Logger(DogStatsDEmitter.class);
  private final static String DRUID_METRIC_SEPARATOR = "\\/";
  private final static String STATSD_SEPARATOR = ":|\\|";

  private final StatsDClient statsd;
  private final DogStatsDEmitterConfig config;
  private final DimensionConverter converter;

  public DogStatsDEmitter(DogStatsDEmitterConfig config, ObjectMapper mapper)
  {
    this.config = config;
    this.converter = new DimensionConverter(mapper, config.getDimensionMapPath());
    statsd = new NonBlockingStatsDClient(
        config.getPrefix(),
        config.getHostname(),
        config.getPort(),
        new String[]{},
        new StatsDClientErrorHandler()
        {
          private int exceptionCount = 0;

          @Override
          public void handle(Exception exception)
          {
            if (exceptionCount % 1000 == 0) {
              log.error(exception, "Error sending metric to StatsD.");
            }
            exceptionCount += 1;
          }
        }
    );
  }


  @Override
  public void start() {}

  @Override
  public void emit(Event event)
  {
    if (event instanceof ServiceMetricEvent) {
      ServiceMetricEvent metricEvent = (ServiceMetricEvent) event;
      String service = metricEvent.getService();
      String metric = metricEvent.getMetric();
      Map<String, Object> userDims = metricEvent.getUserDims();
      Number value = metricEvent.getValue();

      ImmutableList.Builder<String> nameBuilder = new ImmutableList.Builder<>();
      nameBuilder.add(service);
      nameBuilder.add(metric);

      ImmutableList.Builder<String> tagBuilder = new ImmutableList.Builder<>();

      DogStatsDMetric.Type metricType = converter.addFilteredUserDims(
          service,
          metric,
          userDims,
          nameBuilder,
          tagBuilder
      );

      if (metricType != null) {

        String fullName = Joiner.on(config.getSeparator())
                                .join(nameBuilder.build())
                                .replaceAll(DRUID_METRIC_SEPARATOR, config.getSeparator())
                                .replaceAll(STATSD_SEPARATOR, config.getSeparator());

        String[] tags = (String[]) tagBuilder.build().toArray(new String[0]);

        switch (metricType) {
          case count:
            statsd.count(fullName, value.longValue(), tags);
            break;
          case timer:
            statsd.time(fullName, value.longValue(), tags);
            break;
          case gauge:
            statsd.gauge(fullName, value.longValue(), tags);
            break;
        }
      } else {
        log.error("Metric=[%s] has no StatsD type mapping", metric);
      }
    }
  }

  @Override
  public void flush() throws IOException {}

  @Override
  public void close() throws IOException
  {
    statsd.stop();
  }

}
