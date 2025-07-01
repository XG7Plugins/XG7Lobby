package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyCommandListener implements Listener {

    @Override
    public boolean isEnabled() {
        return Config.mainConfigOf(XG7Lobby.getInstance()).get("lobby-teleport-cooldown.dont-move", Boolean.class).orElse(false);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasPermission("xg7lobby.command.lobby.bypass-cooldown")) return;

        CooldownManager cooldownManager = XG7PluginsAPI.cooldowns();

        if (cooldownManager.containsPlayer("lobby-cooldown-before", event.getPlayer())) {
            if (
                    event.getFrom().getBlockX() != event.getTo().getBlockX()
                            || event.getFrom().getBlockY() != event.getTo().getBlockY()
                            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
            ) {
                cooldownManager.removePlayer("lobby-cooldown-before", event.getPlayer().getUniqueId());
            }
        }

    }

}
