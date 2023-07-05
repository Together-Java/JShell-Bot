package org.togetherjava.jshellbot.command;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.togetherjava.jshellbot.Config;
import org.togetherjava.jshellbot.backend.JShellApi;

public class CommandListener extends ListenerAdapter {

  private final List<Command> commands;

  public CommandListener(Config config, JShellApi api) {
    this.commands = List.of(
      new EvalCommand(config, api),
      new CloseCommand(api)
    );
  }

  @Override
  public void onReady(ReadyEvent event) {
    System.out.println("Updating commands");
    for (Guild guild : event.getJDA().getGuilds()) {
      guild.updateCommands()
        .addCommands(commands.stream().map(Command::command).toList())
        .queue();
    }
  }

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    commands.stream().filter(it -> it.name().equals(event.getName()))
      .findFirst()
      .ifPresent(it -> it.handle(event));
  }

}
