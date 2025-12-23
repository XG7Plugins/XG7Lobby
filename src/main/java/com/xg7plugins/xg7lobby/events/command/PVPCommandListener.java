package com.xg7plugins.xg7lobby.events.command;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.cooldowns.CooldownManager;

import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.event.player.PlayerMoveEvent;

public class PVPCommandListener implements Listener {

    @Override
    public boolean isEnabled() {

        ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

        return config.get("enabled", true) && config.child("on-leave-pvp").get("dont-move", true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        CooldownManager cooldownManager = XG7Plugins.getAPI().cooldowns();

        if (cooldownManager.containsPlayer("pvp-disable", event.getPlayer())) {
            if (
                    event.getFrom().getBlockX() != event.getTo().getBlockX()
                            || event.getFrom().getBlockY() != event.getTo().getBlockY()
                            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
            ) {
                cooldownManager.removeCooldown("pvp-disable", event.getPlayer().getUniqueId(), true);
            }
        }

    }

}
