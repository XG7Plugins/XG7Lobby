package com.xg7plugins.xg7lobby.plugin;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class XG7LobbyPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "xg7lobby";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DaviXG7";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.2";
    }
    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return null;
        }
        LobbyPlayer lobbyPlayer = XG7Lobby.getAPI().getLobbyPlayer(player.getUniqueId());
        if (lobbyPlayer == null) {
            return null;
        }

        switch (identifier) {
            case "chat_locked":
                return ConfigFile.of("data/data", XG7Lobby.getInstance()).root().get("chat-locked") + "";
            case "random_lobby_location":
                return XG7Lobby.getAPI().lobbyManager().getRandomLobbyLocation().join() + "";
            case "player_warns":
                return lobbyPlayer.getInfractions().size() + "";
            case "player_is_hiding_players":
                return lobbyPlayer.isHidingPlayers() + "";
            case "player_is_muted":
                return lobbyPlayer.isMuted() + "";
            case "player_time_for_unmute":
                return lobbyPlayer.getUnmuteTime().formatDate("dd/MM/yyyy HH:mm:ss");
            case "player_is_build_enabled":
                return lobbyPlayer.isBuildEnabled() + "";
            case "player_is_flying":
                return lobbyPlayer.isFlying() + "";
            case "player_is_in_pvp":
                return XG7Lobby.getAPI().globalPVPManager().isInPVP(player) + "";
            case "players_in_global_pvp":
                return XG7Lobby.getAPI().globalPVPManager().getAllPlayersInPVP().size() + "";
            case "player_global_pvp_kills":
                return lobbyPlayer.getGlobalPVPKills() + "";
            case "player_global_pvp_deaths":
                return lobbyPlayer.getGlobalPVPDeaths() + "";
            case "player_global_pvp_kdr":
                return (lobbyPlayer.getGlobalPVPDeaths() == 0 ? lobbyPlayer.getGlobalPVPKills() : lobbyPlayer.getGlobalPVPKills() / lobbyPlayer.getGlobalPVPDeaths()) + "";
        }

        return null;
    }
}
