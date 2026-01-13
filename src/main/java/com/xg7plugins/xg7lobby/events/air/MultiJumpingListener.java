package com.xg7plugins.xg7lobby.events.air;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.PlayableSound;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;

import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.UUID;

public class MultiJumpingListener implements Listener {
    private final HashMap<UUID, Integer> jumpingPlayers = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return ConfigFile.mainConfigOf(XG7Lobby.getInstance())
                .section("multi-jumps")
                .get("enabled", true);
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("multi-jumps");

        LobbyPlayer player = XG7Lobby.getAPI().getLobbyPlayer(event.getPlayer().getUniqueId());

        if (player == null) return;
        if (player.getLobbySettings().isFlying() || (jumpingPlayers.containsKey(player.getPlayerUUID()) && jumpingPlayers.get(player.getPlayerUUID()) == 0) || player.getPlayer().getGameMode() == GameMode.CREATIVE || player.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        event.setCancelled(true);

        int power = config.get("power", 2);
        int height = config.get("height", 1);
        player.getPlayer().setVelocity(player.getPlayer().getLocation().getDirection().multiply(power).setY(height));

        PlayableSound sound = config.get("sound", PlayableSound.class);
        if (sound != null) sound.play(player.getPlayer());

        int jumpLimit = config.get("jump-limit", 3);
        jumpingPlayers.putIfAbsent(player.getPlayerUUID(), jumpLimit);
        jumpingPlayers.put(player.getPlayerUUID(), jumpingPlayers.get(player.getPlayer().getUniqueId()) - 1);

        if (jumpingPlayers.get(player.getPlayerUUID()) == 0) player.getPlayer().setAllowFlight(false);

        Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "multi-jump-left", Pair.of("jumps", jumpingPlayers.getOrDefault(player.getPlayerUUID(), jumpLimit) + ""));
    }

    @EventHandler(isOnlyInWorld = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!jumpingPlayers.containsKey(event.getPlayer().getUniqueId())) return;

        boolean disableMultiJumps = ConfigFile.mainConfigOf(XG7Lobby.getInstance())
                .section("pvp")
                .get("disable-multi-jumps", true);

        if (disableMultiJumps && XG7Lobby.getAPI().isPlayerInPVP(event.getPlayer())) return;

        if (event.getPlayer().isOnGround()) {
            event.getPlayer().setAllowFlight(true);
            jumpingPlayers.remove(event.getPlayer().getUniqueId());
        }
    }
}