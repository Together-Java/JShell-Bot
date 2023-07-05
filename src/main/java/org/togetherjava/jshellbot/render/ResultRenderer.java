package org.togetherjava.jshellbot.render;

import static org.togetherjava.jshellbot.render.Colors.ERROR_COLOR;
import static org.togetherjava.jshellbot.render.Colors.SUCCESS_COLOR;
import static org.togetherjava.jshellbot.render.Colors.WARNING_COLOR;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.togetherjava.jshellbot.backend.dto.JShellResult;
import org.togetherjava.jshellbot.backend.dto.SnippetStatus;
import org.togetherjava.jshellbot.util.Strings;

public class ResultRenderer {

  public EmbedBuilder renderToEmbed(User originator, boolean partOfSession, JShellResult result, EmbedBuilder builder) {
    System.out.println(result);
    builder.setAuthor(originator.getEffectiveName() + "'s result");
    builder.setColor(color(result.status()));

    if (result.result() != null && !result.result().isBlank()) {
      builder.addField("Result", result.result(), true);
    }
    if (result.status() == SnippetStatus.ABORTED) {
      builder.setTitle("Request timed out");
    }

    String description = result.errors().isEmpty() ? result.stdout() : String.join(", ", result.errors());
    builder.setDescription(Strings.limitSize(description, MessageEmbed.DESCRIPTION_MAX_LENGTH));

    if (partOfSession) {
      builder.setFooter("Snippet " + result.id() + " of current session");
    } else {
      builder.setFooter("This result is not part of a session");
    }

    return builder;
  }

  private Color color(SnippetStatus status) {
    return switch (status) {
      case VALID -> SUCCESS_COLOR;
      case RECOVERABLE_DEFINED, RECOVERABLE_NOT_DEFINED -> WARNING_COLOR;
      case REJECTED, ABORTED -> ERROR_COLOR;
    };
  }

}
