package com.xg7plugins.xg7lobby.events.air;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.LaunchpadConfigs;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class LaunchpadListener implements Listener {


    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), LaunchpadConfigs.class).isEnabled();
    }

    @EventHandler(isOnlyInWorld = true)
    public void onMove(PlayerMoveEvent event) {

        LaunchpadConfigs config = Config.of(XG7Lobby.getInstance(), LaunchpadConfigs.class);

        Player player = event.getPlayer();

        if (Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isDisableLaunchpad() && XG7LobbyAPI.isPlayerInPVP(player)) return;

        if (player.getLocation().getBlock().getType() == config.getTopBlock().get() && player.getLocation().subtract(0, 1, 0).getBlock().getType() == config.getBottomBlock().get()) {

            player.setVelocity(player.getLocation().getDirection().multiply(config.getPower()).setY(config.getHeight()));

            config.getSound().play(player);
        }
    }
}
