package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.cooldowns.CooldownManager;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import org.bukkit.event.player.PlayerMoveEvent;

public class PVPCommandListener implements Listener {

    @Override
    public boolean isEnabled() {

        PVPConfigs config = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        return config.isEnabled() && config.getOnLeaveConfigs().isDontMove();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        CooldownManager cooldownManager = XG7PluginsAPI.cooldowns();

        if (cooldownManager.containsPlayer("pvp-disable", event.getPlayer())) {
            if (
                    event.getFrom().getBlockX() != event.getTo().getBlockX()
                            || event.getFrom().getBlockY() != event.getTo().getBlockY()
                            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
            ) {
                cooldownManager.removeCooldown("pvp-disable", event.getPlayer().getUniqueId());
            }
        }

    }

}
