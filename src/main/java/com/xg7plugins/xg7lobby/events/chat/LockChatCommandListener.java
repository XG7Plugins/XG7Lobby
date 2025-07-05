package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import com.xg7plugins.xg7lobby.configs.XG7LobbyEnvironment;
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

            if (Config.of(XG7Lobby.getInstance(), ChatConfigs.class).isLockChatOnlyOnLobby() && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

            Text.sendTextFromLang(event.getPlayer(),XG7Lobby.getInstance(), "chat.locked");
            event.setCancelled(true);

        }
    }
}
