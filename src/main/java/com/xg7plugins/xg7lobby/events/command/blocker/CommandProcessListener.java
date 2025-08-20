package com.xg7plugins.xg7lobby.events.command.blocker;

import com.xg7plugins.commands.CommandMessages;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandProcessListener implements Listener {

    private final ChatConfigs config = Config.of(XG7Lobby.getInstance(), ChatConfigs.class);

    @Override
    public boolean isEnabled() {
        return config.isBlockCommandsEnabled();
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String label = message.split(" ")[0].substring(1);

        if (player.hasPermission("xg7lobby.command_block_bypass")) return;

        List<String> commands = config.getBlockedCommands();

        if (commands.contains(label)) {
            CommandMessages.COMMAND_NOT_FOUND.send(player);
            event.setCancelled(true);
        }
    }
}
