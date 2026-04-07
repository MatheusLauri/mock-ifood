package com.mockifood.ifood.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mock.ifood")
public record IfoodMockProperties(
    long semente,
    Caos caos
) {
  public record Caos(
      boolean enabled,
      int delayMsMin,
      int delayMsMax,
      double errorRate,
      double timeoutRate,
      int timeoutMs
  ) {}
}

