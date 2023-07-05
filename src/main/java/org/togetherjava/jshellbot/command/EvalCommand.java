package org.togetherjava.jshellbot.command;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.togetherjava.jshellbot.Config;
import org.togetherjava.jshellbot.backend.JShellApi;
import org.togetherjava.jshellbot.backend.dto.JShellResult;
import org.togetherjava.jshellbot.render.Colors;
import org.togetherjava.jshellbot.render.ResultRenderer;
import org.togetherjava.jshellbot.util.RateLimiter;
import org.togetherjava.jshellbot.util.ResponseUtils.RequestFailedException;

public class EvalCommand implements Command {

  private final JShellApi api;
  private final ResultRenderer renderer;
  private final RateLimiter<Long> rateLimiter;

  public EvalCommand(Config config, JShellApi api) {
    this.api = api;
    this.renderer = new ResultRenderer();

    this.rateLimiter = new RateLimiter<>(
      Duration.ofSeconds(config.rateLimitWindowSeconds()),
      config.rateLimitRequestsInWindow()
    );
  }

  @Override
  public String name() {
    return "eval";
  }

  @Override
  public CommandData command() {
    return Commands
      .slash(name(), "Evaluates java code")
      .addOption(OptionType.STRING, "code", "The code snippet to execute", true)
      .addOption(OptionType.BOOLEAN, "oneoff", "Evaluate the command without opening a session");
  }

  @Override
  public void handle(SlashCommandInteraction interaction) {
    User user = interaction.getUser();
    String code = interaction.getOption("code").getAsString();
    boolean oneOffSession = Optional.ofNullable(interaction.getOption("oneoff"))
      .map(OptionMapping::getAsBoolean)
      .orElse(false);

    interaction.deferReply().queue(interactionHook -> {
      try {
        evaluateAndRespond(user, code, oneOffSession, interactionHook);
      } catch (RequestFailedException e) {
        interactionHook.editOriginalEmbeds(
          new EmbedBuilder()
            .setAuthor(user.getEffectiveName() + "'s result")
            .setColor(Colors.ERROR_COLOR)
            .setDescription("Request failed: " + e.getMessage())
            .build()
        ).queue();
      }
    });
  }

  private void evaluateAndRespond(User user, String code, boolean oneOffSession, InteractionHook interactionHook) {
    JShellResult result;
    if (oneOffSession) {
      if (wasRateLimited(interactionHook, user, Instant.now())) {
        return;
      }
      result = api.evalOnce(code);
    } else {
      result = api.evalSession(code, user.getId());
    }

    MessageEmbed embed = renderer.renderToEmbed(
      user,
      !oneOffSession,
      result,
      new EmbedBuilder()
    ).build();

    interactionHook.editOriginalEmbeds(embed).queue();
  }

  private boolean wasRateLimited(InteractionHook interaction, User user, Instant checkTime) {
    if (rateLimiter.allowRequest(user.getIdLong(), checkTime)) {
      return false;
    }

    String nextAllowedTime = TimeFormat.RELATIVE.format(
      rateLimiter.nextAllowedRequestTime(user.getIdLong(), checkTime)
    );
    interaction.editOriginalEmbeds(
      new EmbedBuilder()
        .setAuthor(user.getEffectiveName() + "'s result")
        .setDescription("You are currently ratelimited. Please try again " + nextAllowedTime + ".")
        .setColor(Colors.ERROR_COLOR)
        .build()
    ).queue();

    return true;
  }

}
