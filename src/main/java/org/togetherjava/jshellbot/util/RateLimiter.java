package org.togetherjava.jshellbot.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RateLimiter<T> {

  private final Map<T, List<Instant>> lastUses;

  private final Duration duration;
  private final int allowedRequests;

  public RateLimiter(Duration duration, int allowedRequests) {
    this.duration = duration;
    this.allowedRequests = allowedRequests;

    this.lastUses = new HashMap<>();
  }

  public boolean allowRequest(T key, Instant time) {
    synchronized (lastUses) {
      List<Instant> usesInWindow = getEffectiveUses(key, time);

      if (usesInWindow.size() >= allowedRequests) {
        return false;
      }
      usesInWindow.add(time);

      lastUses.put(key, usesInWindow);

      return true;
    }
  }

  private List<Instant> getEffectiveUses(T key, Instant time) {
    return lastUses.getOrDefault(key, List.of())
      .stream()
      .filter(it -> Duration.between(it, time).compareTo(duration) <= 0)
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public Instant nextAllowedRequestTime(T key, Instant time) {
    synchronized (lastUses) {
      List<Instant> currentUses = getEffectiveUses(key, time);
      currentUses.sort(Instant::compareTo);

      if (currentUses.size() < allowedRequests) {
        return Instant.now();
      }

      return currentUses.get(0).plus(duration);
    }
  }

}
