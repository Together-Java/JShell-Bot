package org.togetherjava.jshellbot.command;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command {

  String name();

  CommandData command();

  void handle(SlashCommandInteraction interaction);

}
