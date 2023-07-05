package org.togetherjava.jshellbot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Config(
  @JsonProperty("token") String token,
  @JsonProperty("baseUrl") String baseUrl,
  @JsonProperty("rateLimitWindowSeconds") int rateLimitWindowSeconds,
  @JsonProperty("rateLimitRequestsInWindow") int rateLimitRequestsInWindow
) {

}
