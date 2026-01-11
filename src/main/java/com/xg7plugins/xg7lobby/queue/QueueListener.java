package com.xg7plugins.xg7lobby.queue;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.AllArgsConstructor;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class QueueListener implements Listener {

    private final QueueManager queueManager;

    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("queue").get("enabled");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        queueManager.removeFromAllQueues(event.getPlayer());
    }
}
