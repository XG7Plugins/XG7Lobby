package com.xg7plugins.xg7lobby.events.air_events;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class LaunchpadListener implements Listener {

    private final boolean enabled;

    private final XMaterial topBlock;
    private final XMaterial bottomBlock;

    private final double power;
    private final double height;

    private final Sound sound;
    private final float soundVolume;
    private final float soundPitch;

    public LaunchpadListener() {
        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        enabled = config.get("launchpad.enabled", Boolean.class).orElse(false);

        topBlock = config.get("launchpad.top-block", XMaterial.class).orElse(XMaterial.AIR);
        bottomBlock = config.get("launchpad.bottom-block", XMaterial.class).orElse(XMaterial.AIR);

        power = config.get("launchpad.power", Double.class).orElse(1.0);
        height = config.get("launchpad.height", Double.class).orElse(1.0);

        String[] soundString = config.get("launchpad.sound", String.class).orElse("ENTITY_BAT_TAKEOFF, 1, 1").split(", ");

        sound = XSound.of(soundString[0]).orElse(XSound.ENTITY_BAT_TAKEOFF).get();
        soundVolume = Float.parseFloat(soundString[1]);
        soundPitch = Float.parseFloat(soundString[2]);
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @EventHandler(isOnlyInWorld = true)
    public void onMove(PlayerMoveEvent event) {

//        if (XG7Lobby.getInstance().getGlobalPVPManager().isPlayerInPVP(event.getPlayer()) && config.get("global-pvp.disable-launchpad", Boolean.class).orElse(true)) return;

        Player player = event.getPlayer();

        if (player.getLocation().getBlock().getType() == topBlock.get() && player.getLocation().subtract(0, 1, 0).getBlock().getType() == bottomBlock.get()) {

            player.setVelocity(player.getLocation().getDirection().multiply(power).setY(height));

            player.getWorld().playSound(player.getLocation(), sound, soundVolume, soundPitch);
        }
    }
}
