package io.druid.emitter.dogstatsd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 */
public class DogStatsDEmitterConfig
{

  @JsonProperty
  final private String hostname;
  @JsonProperty
  final private Integer port;
  @JsonProperty
  final private String prefix;
  @JsonProperty
  final private String separator;
  @JsonProperty
  final private Boolean includeHost;
  @JsonProperty
  final private String dimensionMapPath;

  @JsonCreator
  public DogStatsDEmitterConfig(
      @JsonProperty("hostname") String hostname,
      @JsonProperty("port") Integer port,
      @JsonProperty("prefix") String prefix,
      @JsonProperty("separator") String separator,
      @JsonProperty("includeHost") Boolean includeHost,
      @JsonProperty("dimensionMapPath") String dimensionMapPath
  )
  {
    this.hostname = Preconditions.checkNotNull(hostname, "StatsD hostname cannot be null.");
    this.port = Preconditions.checkNotNull(port, "StatsD port cannot be null.");
    this.prefix = prefix != null ? prefix : "";
    this.separator = separator != null ? separator : ".";
    this.includeHost = includeHost != null ? includeHost : false;
    this.dimensionMapPath = dimensionMapPath;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DogStatsDEmitterConfig that = (DogStatsDEmitterConfig) o;

    if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) {
      return false;
    }
    if (port != null ? !port.equals(that.port) : that.port != null) {
      return false;
    }
    if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) {
      return false;
    }
    if (separator != null ? !separator.equals(that.separator) : that.separator != null) {
      return false;
    }
    if (includeHost != null ? !includeHost.equals(that.includeHost) : that.includeHost != null) {
      return false;
    }
    return dimensionMapPath != null ? dimensionMapPath.equals(that.dimensionMapPath) : that.dimensionMapPath == null;

  }

  @Override
  public int hashCode()
  {
    int result = hostname != null ? hostname.hashCode() : 0;
    result = 31 * result + (port != null ? port.hashCode() : 0);
    result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
    result = 31 * result + (separator != null ? separator.hashCode() : 0);
    result = 31 * result + (includeHost != null ? includeHost.hashCode() : 0);
    result = 31 * result + (dimensionMapPath != null ? dimensionMapPath.hashCode() : 0);
    return result;
  }

  @JsonProperty
  public String getHostname()
  {
    return hostname;
  }

  @JsonProperty
  public int getPort()
  {
    return port;
  }

  @JsonProperty
  public String getPrefix()
  {
    return prefix;
  }

  @JsonProperty
  public String getSeparator()
  {
    return separator;
  }

  @JsonProperty
  public Boolean getIncludeHost()
  {
    return includeHost;
  }

  @JsonProperty
  public String getDimensionMapPath()
  {
    return dimensionMapPath;
  }
}
