package com.xg7plugins.xg7lobby.commands.custom;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomCommand {

    private final String name;
    private final String syntax;
    private final List<String> aliases;
    private final String description;
    private final String permission;
    private final List<String> actions;

    private final boolean enabledWorldOnly;

    public CustomCommand(String path) {

        ConfigSection config = ConfigFile.of("custom_commands", XG7Lobby.getInstance()).section("custom-commands." + path);

        this.name = config.get("name", String.class);

        if (this.name == null) throw new IllegalArgumentException("The name of the custom command cannot be null!");

        this.syntax = config.get("syntax", "No syntax");
        this.description = config.get("description", "No description");
        this.permission = config.get("permission", "");
        this.aliases = config.getList("aliases", String.class).orElse(new ArrayList<>());
        this.actions = config.getList("actions", String.class).orElse(new ArrayList<>());

        this.enabledWorldOnly = config.get("execute-only-in-an-enabled-world", false);

    }

    public void execute(Player player) {
        ActionsProcessor.process(actions, player);
    }

}
