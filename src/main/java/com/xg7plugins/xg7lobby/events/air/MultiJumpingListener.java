package com.xg7plugins.xg7lobby.events.air;

import com.cryptomorin.xseries.XSound;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.LaunchpadConfigs;
import com.xg7plugins.xg7lobby.configs.MultiJumpsConfigs;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.UUID;

public class MultiJumpingListener implements Listener {
    private final HashMap<UUID, Integer> jumpingPlayers = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), MultiJumpsConfigs.class).isEnabled();
    }


    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {

        MultiJumpsConfigs config = Config.of(XG7Lobby.getInstance(), MultiJumpsConfigs.class);

        LobbyPlayer player = XG7LobbyAPI.getLobbyPlayer(event.getPlayer().getUniqueId());

        if (player == null) return;
        if (player.isFlying() || (jumpingPlayers.containsKey(player.getPlayerUUID()) && jumpingPlayers.get(player.getPlayerUUID()) == 0) || player.getPlayer().getGameMode() == GameMode.CREATIVE || player.getPlayer().getGameMode() == GameMode.SPECTATOR) return;

        event.setCancelled(true);

        player.getPlayer().setVelocity(player.getPlayer().getLocation().getDirection().multiply(config.getPower()).setY(config.getHeight()));
        config.getSound().play(player.getPlayer());

        jumpingPlayers.putIfAbsent(player.getPlayerUUID(), config.getJumpLimit());
        jumpingPlayers.put(player.getPlayerUUID(), jumpingPlayers.get(player.getPlayer().getUniqueId()) - 1);

        if (jumpingPlayers.get(player.getPlayerUUID()) == 0) player.getPlayer().setAllowFlight(false);

        Text.sendTextFromLang(player.getPlayer(), XG7Lobby.getInstance(), "multi-jump-left", Pair.of("jumps", jumpingPlayers.getOrDefault(player.getPlayerUUID(), config.getJumpLimit()) + ""));

    }

    @EventHandler(isOnlyInWorld = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!jumpingPlayers.containsKey(event.getPlayer().getUniqueId())) return;

        if (Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isDisableMultiJumps() && XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;

        if (event.getPlayer().isOnGround()) {
            event.getPlayer().setAllowFlight(true);
            jumpingPlayers.remove(event.getPlayer().getUniqueId());
        }

    }
}
