package com.xg7plugins.xg7lobby.events.air_events;

import com.cryptomorin.xseries.XSound;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.UUID;

public class MultiJumpingListener implements Listener {
    private final HashMap<UUID, Integer> jumpingPlayers = new HashMap<>();

    private final boolean enabled;

    private final int jumpLimit;

    private final double power;
    private final double height;

    private final Sound sound;
    private final float soundVolume;
    private final float soundPitch;

    public MultiJumpingListener() {
        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        enabled = config.get("multi-jumps.enabled", Boolean.class).orElse(false);

        jumpLimit = config.get("multi-jumps.jump-limit", Integer.class).orElse(10);

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


    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(event.getPlayer().getUniqueId());

        if (player == null) return;
        if (player.isFlying() || (jumpingPlayers.containsKey(player.getPlayerUUID()) && jumpingPlayers.get(player.getPlayerUUID()) == 0) || player.getPlayer().getGameMode() == GameMode.CREATIVE || player.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        event.setCancelled(true);

        player.getPlayer().setVelocity(player.getPlayer().getLocation().getDirection().multiply(power).setY(height));
        player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), sound, soundVolume, soundPitch);

        jumpingPlayers.putIfAbsent(player.getPlayerUUID(), jumpLimit);
        jumpingPlayers.put(player.getPlayerUUID(), jumpingPlayers.get(player.getPlayer().getUniqueId()) - 1);

        if (jumpingPlayers.get(player.getPlayerUUID()) == 0) player.getPlayer().setAllowFlight(false);

        Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "multi-jump-left", Pair.of("jumps", jumpingPlayers.getOrDefault(player.getPlayerUUID(), jumpLimit) + ""));

    }

    @EventHandler(isOnlyInWorld = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!jumpingPlayers.containsKey(event.getPlayer().getUniqueId())) return;

        //        if (Config.mainConfigOf(XG7Lobby.getInstance()).get("global-pvp.disable-multi-jumps", Boolean.class).orElse(false) && XG7Lobby.getInstance().getGlobalPVPManager().isPlayerInPVP(event.getPlayer())) return;

        if (event.getPlayer().isOnGround()) {
            event.getPlayer().setAllowFlight(true);
            jumpingPlayers.remove(event.getPlayer().getUniqueId());
        }

    }
}
