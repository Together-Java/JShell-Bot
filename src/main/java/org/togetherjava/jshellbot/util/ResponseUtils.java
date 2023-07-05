package org.togetherjava.jshellbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.Optional;

public class ResponseUtils {

  public static <T> BodyHandler<T> ofJson(Class<T> t, ObjectMapper mapper) {
    return responseInfo -> BodySubscribers.mapping(
      BodySubscribers.ofByteArray(),
      bytes -> {
        if (responseInfo.statusCode() == 200 || responseInfo.statusCode() == 204) {
          return uncheckedParseJson(t, mapper, bytes);
        }
        String errorMessage = tryParseError(bytes, mapper)
          .orElse("Request failed with status: " + responseInfo.statusCode());
        throw new RequestFailedException(errorMessage);
      }
    );
  }

  private static <T> T uncheckedParseJson(Class<T> t, ObjectMapper mapper, byte[] value) {
    try {
      return mapper.readValue(value, t);
    } catch (IOException e) {
      throw new UncheckedIOException("Error parsing json", e);
    }
  }

  private static Optional<String> tryParseError(byte[] bytes, ObjectMapper mapper) {
    try {
      return Optional.ofNullable(mapper.readTree(bytes).get("error").asText());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static class RequestFailedException extends RuntimeException {

    public RequestFailedException(String message) {
      super(message);
    }

  }

}
