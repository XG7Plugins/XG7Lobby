package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;

public class BlockCommandsListener implements Listener {
    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("block-commands").get("enabled");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("xg7lobby.command.*") || event.getPlayer().hasPermission("xg7lobby.command_block_bypass"))
            return;

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("block-commands");

        if (config.getList("blocked-commands", String.class).orElse(Collections.emptyList()).contains(event.getMessage().split(" ")[0])) {
            event.setCancelled(true);
            Text.fromLang(event.getPlayer(), XG7Lobby.getInstance(), "chat.prohibited-command").thenAccept(text -> text.send(event.getPlayer()));
        }
    }
}