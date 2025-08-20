package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BlockCommandsListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), ChatConfigs.class).isBlockCommandsEnabled();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("xg7lobby.command.*") || event.getPlayer().hasPermission("xg7lobby.command_block_bypass")) return;

        ChatConfigs config = Config.of(XG7Lobby.getInstance(), ChatConfigs.class);

        if (config.getBlockedCommands().contains(event.getMessage().split(" ")[0])) {
            event.setCancelled(true);
            Text.fromLang(event.getPlayer(),XG7Lobby.getInstance(), "chat.prohibited-command").thenAccept(text -> text.send(event.getPlayer()));
        }
    }
}
