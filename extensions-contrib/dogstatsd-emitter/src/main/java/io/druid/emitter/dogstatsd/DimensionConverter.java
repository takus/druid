package io.druid.emitter.dogstatsd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.metamx.common.ISE;
import com.metamx.common.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 */
public class DimensionConverter
{

  private final static Logger log = new Logger(DimensionConverter.class);
  private Map<String, DogStatsDMetric> metricMap;

  public DimensionConverter(ObjectMapper mapper, String dimensionMapPath)
  {
    metricMap = readMap(mapper, dimensionMapPath);
  }

  public DogStatsDMetric.Type addFilteredUserDims(
      String service,
      String metric,
      Map<String, Object> userDims,
      ImmutableList.Builder<String> nameBuilder,
      ImmutableList.Builder<String> tagBuilder
  )
  {
     /*
        Find the metric in the map. If we cant find it try to look it up prefixed by the service name.
        This is because some metrics are reported differently, but with the same name, from different services.
       */
    DogStatsDMetric statsDMetric = null;
    if (metricMap.containsKey(metric)) {
      statsDMetric = metricMap.get(metric);
    } else if (metricMap.containsKey(service + "-" + metric)) {
      statsDMetric = metricMap.get(service + "-" + metric);
    }
    if (statsDMetric != null) {
      for (String dim : statsDMetric.dimensions) {
        if (userDims.containsKey(dim)) {
          tagBuilder.add(dim + ":" + userDims.get(dim).toString());
        }
      }
      return statsDMetric.type;
    } else {
      return null;
    }
  }

  private Map<String, DogStatsDMetric> readMap(ObjectMapper mapper, String dimensionMapPath)
  {
    try {
      InputStream is;
      if (Strings.isNullOrEmpty(dimensionMapPath)) {
        log.info("Using default metric dimension and types");
        is = this.getClass().getClassLoader().getResourceAsStream("defaultMetricDimensions.json");
      } else {
        log.info("Using metric dimensions at types at [%s]", dimensionMapPath);
        is = new FileInputStream(new File(dimensionMapPath));
      }
      return mapper.reader(new TypeReference<Map<String, DogStatsDMetric>>()
      {
      }).readValue(is);
    }
    catch (IOException e) {
      throw new ISE(e, "Failed to parse metric dimensions and types");
    }
  }
}
