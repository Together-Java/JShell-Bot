package org.togetherjava.jshellbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.togetherjava.jshellbot.backend.JShellApi;
import org.togetherjava.jshellbot.command.CommandListener;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    TomlMapper mapper = TomlMapper.builder().build();

    Config config = mapper.readValue(
      Files.readString(Path.of(args[0])),
      Config.class
    );

    JShellApi api = new JShellApi(new ObjectMapper(), config.baseUrl());

    JDA jda = JDABuilder.createDefault(config.token())
      .addEventListeners(new CommandListener(config, api))
      .build()
      .awaitReady();

    System.out.println("Invite me at: " + jda.getInviteUrl());
  }

}
