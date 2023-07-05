package org.togetherjava.jshellbot.backend.dto;

import java.util.List;

public record JShellResult(
  SnippetStatus status,
  SnippetType type,
  String id,
  String source,
  String result,
  boolean stdoutOverflow,
  String stdout,
  List<String> errors
) {

  public JShellResult {
    errors = List.copyOf(errors);
  }
}
