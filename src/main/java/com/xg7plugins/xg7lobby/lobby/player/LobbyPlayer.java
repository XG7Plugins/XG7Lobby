package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.utils.time.Time;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class LobbyPlayer implements Entity<UUID, LobbyPlayer> {

    private final UUID id;
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
    private final List<Warning> infractions = new ArrayList<>();

    public LobbyPlayer(UUID id) {
        this.id = id;
    }

    private LobbyPlayer() {
        id = null;
    }


    @Override
    public boolean equals(LobbyPlayer other) {
        return other.getID().equals(this.getID());
    }

    @Override
    public UUID getID() {
        return id;
    }
}
