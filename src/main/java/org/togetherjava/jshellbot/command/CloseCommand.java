package org.togetherjava.jshellbot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.togetherjava.jshellbot.backend.JShellApi;
import org.togetherjava.jshellbot.render.Colors;

public class CloseCommand implements Command {

  private final JShellApi api;

  public CloseCommand(JShellApi api) {
    this.api = api;
  }

  @Override
  public String name() {
    return "close";
  }

  @Override
  public CommandData command() {
    return Commands.slash(name(), "Closes and clears your session");
  }

  @Override
  public void handle(SlashCommandInteraction interaction) {
    api.closeSession(interaction.getUser().getId());

    interaction.replyEmbeds(
      new EmbedBuilder()
        .setColor(Colors.SUCCESS_COLOR)
        .setAuthor(interaction.getUser().getEffectiveName())
        .setTitle("Session closed")
        .build()
    ).queue();
  }

}
