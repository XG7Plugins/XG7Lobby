package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.data.database.entity.Entity;

import java.util.UUID;

public class LobbyPlayer implements Entity<UUID, LobbyPlayer> {

    private final UUID id;
    private final boolean hidingPlayers;
    private final boolean muted;
    private final




    @Override
    public boolean equals(LobbyPlayer other) {
        return other.getID().equals(this.getID());
    }

    @Override
    public UUID getID() {
        return null;
    }
}
