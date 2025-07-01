package com.xg7plugins.xg7lobby.commands.custom;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
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

        Config config = Config.of("custom_commands", XG7Lobby.getInstance());

        this.name = config.get("custom-commands." + path + ".name", String.class).orElseThrow(() -> new RuntimeException("This command don't have a name!"));
        this.syntax = config.get("custom-commands." + path + ".syntax", String.class).orElse("No syntax");
        this.description = config.get("custom-commands." + path + ".description", String.class).orElse("No description");
        this.permission = config.get("custom-commands." + path + ".permission", String.class).orElse("");
        this.aliases = config.getList("custom-commands." + path + ".aliases", String.class).orElse(new ArrayList<>());
        this.actions = config.getList("custom-commands." + path + ".actions", String.class).orElse(new ArrayList<>());

        this.enabledWorldOnly = config.get("custom-commands." + path + "execute-only-in-an-enabled-world", Boolean.class).orElse(false);

    }

    public void execute(Player player) {
        ActionsProcessor.process(actions, player);
    }

}
