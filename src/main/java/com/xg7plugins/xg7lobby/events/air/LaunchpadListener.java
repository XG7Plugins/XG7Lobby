package com.xg7plugins.xg7lobby.events.air;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.utils.PlayableSound;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class LaunchpadListener implements Listener {

    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance())
                .section("launchpad")
                .get("enabled", true);
    }

    @EventHandler(isOnlyInWorld = true)
    public void onMove(PlayerMoveEvent event) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).section("launchpad");

        Player player = event.getPlayer();

        boolean disableLaunchpad = ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance())
                .section("pvp")
                .get("disable-launchpad", true);

        if (disableLaunchpad && XG7LobbyAPI.isPlayerInPVP(player)) return;

        XMaterial topBlock = config.get("top-block", XMaterial.STONE_PRESSURE_PLATE);
        XMaterial bottomBlock = config.get("bottom-block", XMaterial.REDSTONE_BLOCK);

        if (player.getLocation().getBlock().getType() == topBlock.parseMaterial() &&
                player.getLocation().subtract(0, 1, 0).getBlock().getType() == bottomBlock.parseMaterial()) {

            int power = config.get("power", 3);
            int height = config.get("height", 1);
            player.setVelocity(player.getLocation().getDirection().multiply(power).setY(height));

            PlayableSound sound = config.get("sound", PlayableSound.class);
            if (sound != null) {
                sound.play(player);
            }
        }
    }
}