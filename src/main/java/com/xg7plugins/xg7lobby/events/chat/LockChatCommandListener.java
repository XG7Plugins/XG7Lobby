package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.environment.XG7LobbyEnvironment;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LockChatCommandListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChatWhenLock(AsyncPlayerChatEvent event) {
        XG7LobbyEnvironment xg7LobbyConfig = XG7Lobby.getInstance().getEnvironmentConfig();

        if (event.getPlayer().hasPermission("xg7lobby.chat.ignore-lock")) return;
        if (xg7LobbyConfig.isChatLocked()) {

            if (ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("lock-chat-only-in-lobby", false) && !XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

            Text.sendTextFromLang(event.getPlayer(),XG7Lobby.getInstance(), "chat.locked");
            event.setCancelled(true);

        }
    }
}
