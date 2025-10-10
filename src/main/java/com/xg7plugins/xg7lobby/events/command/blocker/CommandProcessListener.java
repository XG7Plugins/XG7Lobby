package com.xg7plugins.xg7lobby.events.command.blocker;

import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;
import java.util.List;

public class CommandProcessListener implements Listener {

    private final ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("block-commands");

    @Override
    public boolean isEnabled() {
        return config.get("enabled");
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String label = message.split(" ")[0].substring(1);

        if (player.hasPermission("xg7lobby.command_block_bypass")) return;

        List<String> commands = config.getList("commands", String.class).orElse(Collections.emptyList());

        if (commands.contains(label)) {
            CommandState.COMMAND_NOT_FOUND.send(player);
            event.setCancelled(true);
        }
    }
}