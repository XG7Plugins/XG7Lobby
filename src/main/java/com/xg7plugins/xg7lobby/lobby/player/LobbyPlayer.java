package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class LobbyPlayer implements Entity<UUID, LobbyPlayer> {

    private final UUID playerUUID;
    private boolean muted;

    @Column(name = "unmute_time")
    private Time unmuteTime;

    @Column(name = "hiding_players")
    private boolean hidingPlayers;
    @Column(name = "build_enabled")
    private boolean buildEnabled;
    @Column(name = "fly_enabled")
    private boolean flying;
    @Column(name = "global_pvp_kills")
    private int globalPVPKills;
    @Column(name = "global_pvp_deaths")
    private int globalPVPDeaths;
    private final List<Infraction> infractions = new ArrayList<>();

    public LobbyPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    private LobbyPlayer() {
        playerUUID = null;
    }


    @Override
    public boolean equals(LobbyPlayer other) {
        return other.getID().equals(this.getID());
    }

    @Override
    public UUID getID() {
        return playerUUID;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getServer().getOfflinePlayer(playerUUID);
    }
    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(playerUUID);
    }

    public void fly() {
        Player player = this.getPlayer();

        if (player == null) return;

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());


        if (!XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), player)) return;

        player.setAllowFlight(
                flying || (
                                (config.get("multi-jumps.enabled", Boolean.class).orElse(false) && player.hasPermission("xg7lobby.multi-jumps"))
                                || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR
                )
        );
        if (player.getAllowFlight()) player.setFlying(flying);

    }

}
