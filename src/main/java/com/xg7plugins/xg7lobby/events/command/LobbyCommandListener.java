package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.LobbyTeleportConfigs;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyCommandListener implements Listener {

    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), LobbyTeleportConfigs.class).isDontMove();
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
                cooldownManager.removeCooldown("lobby-cooldown-before", event.getPlayer().getUniqueId(), true);
            }
        }

    }

}
