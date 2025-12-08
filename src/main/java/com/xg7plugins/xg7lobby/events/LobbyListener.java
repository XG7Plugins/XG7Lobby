package com.xg7plugins.xg7lobby.events;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public interface LobbyListener extends Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    default void onWorldChange(PlayerTeleportEvent event) {

        List<String> enabledWorlds = XG7Plugins.getAPI().getEnabledWorldsOf(XG7Lobby.getInstance());
        if (!enabledWorlds.contains(event.getFrom().getWorld().getName()) && enabledWorlds.contains(event.getTo().getWorld().getName())) {
            onWorldJoin(event.getPlayer(), event.getTo().getWorld());
            return;
        }
        if (enabledWorlds.contains(event.getFrom().getWorld().getName()) && !enabledWorlds.contains(event.getTo().getWorld().getName())) {
            onWorldLeave(event.getPlayer(), event.getTo().getWorld());
        }
    
    }

    default void onWorldJoin(Player player, World newWorld) {}
    default void onWorldLeave(Player player, World newWorld) {}

}
