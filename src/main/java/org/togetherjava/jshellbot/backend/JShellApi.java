package org.togetherjava.jshellbot.backend;

import static org.togetherjava.jshellbot.util.ResponseUtils.ofJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import org.togetherjava.jshellbot.backend.dto.JShellResult;
import org.togetherjava.jshellbot.backend.dto.JShellResultWithId;
import org.togetherjava.jshellbot.util.ResponseUtils.RequestFailedException;

public class JShellApi {

  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;
  private final String baseUrl;

  public JShellApi(ObjectMapper objectMapper, String baseUrl) {
    this.objectMapper = objectMapper;
    this.baseUrl = baseUrl;

    this.httpClient = HttpClient.newBuilder().build();
  }

  public JShellResult evalOnce(String code) {
    return send(
      baseUrl + "eval",
      HttpRequest.newBuilder().POST(BodyPublishers.ofString(code)),
      ofJson(JShellResultWithId.class, objectMapper)
    )
      .body()
      .result();
  }

  public JShellResult evalSession(String code, String sessionId) {
    return send(
      baseUrl + "eval/" + sessionId,
      HttpRequest.newBuilder().POST(BodyPublishers.ofString(code)),
      ofJson(JShellResult.class, objectMapper)
    )
      .body();
  }

  public void closeSession(String sessionId) {
    send(
      baseUrl + "/" + sessionId,
      HttpRequest.newBuilder().DELETE(),
      BodyHandlers.discarding()
    ).body();
  }

  private <T> HttpResponse<T> send(String url, HttpRequest.Builder builder, BodyHandler<T> body) {
    try {
      return httpClient.send(
        builder.uri(new URI(url)).build(),
        body
      );
    } catch (IOException e) {
      if (e.getCause() instanceof RequestFailedException r) {
        throw r;
      }
      throw new UncheckedIOException(e);
    } catch (InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
