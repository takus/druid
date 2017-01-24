package io.druid.emitter.dogstatsd;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.metamx.emitter.core.Emitter;
import io.druid.guice.JsonConfigProvider;
import io.druid.guice.ManageLifecycle;
import io.druid.initialization.DruidModule;

import java.util.Collections;
import java.util.List;

/**
 */
public class DogStatsDEmitterModule implements DruidModule
{
  private static final String EMITTER_TYPE = "dogstatsd";

  @Override
  public List<? extends Module> getJacksonModules()
  {
    return Collections.EMPTY_LIST;
  }

  @Override
  public void configure(Binder binder)
  {
    JsonConfigProvider.bind(binder, "druid.emitter." + EMITTER_TYPE, DogStatsDEmitterConfig.class);
  }

  @Provides
  @ManageLifecycle
  @Named(EMITTER_TYPE)
  public Emitter getEmitter(DogStatsDEmitterConfig config, ObjectMapper mapper)
  {
    return new DogStatsDEmitter(config, mapper);
  }
}
