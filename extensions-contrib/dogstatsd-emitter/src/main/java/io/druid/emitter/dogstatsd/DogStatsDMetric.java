package io.druid.emitter.dogstatsd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SortedSet;

/**
 */
public class DogStatsDMetric
{
  public final SortedSet<String> dimensions;
  public final Type type;

  @JsonCreator
  public DogStatsDMetric(
      @JsonProperty("dimensions") SortedSet<String> dimensions,
      @JsonProperty("type") Type type
  )
  {
    this.dimensions = dimensions;
    this.type = type;
  }

  public enum Type
  {
    count, gauge, timer
  }
}
