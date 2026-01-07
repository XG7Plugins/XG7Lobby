package com.xg7plugins.xg7lobby.events.chat;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LockChatCommandListener implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChatWhenLock(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("xg7lobby.chat.ignore-lock")) return;
        if (ConfigFile.of("data/data", XG7Lobby.getInstance()).root().get("chat-locked")) {

            if (ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("lock-chat-only-in-lobby", false) && !XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), event.getPlayer())) return;

            Text.sendTextFromLang(event.getPlayer(), XG7Lobby.getInstance(), "chat.locked");
            event.setCancelled(true);

        }
    }
}
