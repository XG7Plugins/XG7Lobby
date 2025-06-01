package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.data.database.entity.*;
import com.xg7plugins.utils.ShortUUID;
import lombok.Data;

import java.util.UUID;

@Data
@Table(name = "infractions")
public class Infraction implements Entity<String, Infraction> {

    @Pkey
    @Column(name = "id", length = 16)
    private String id;
    @FKey(origin_table = LobbyPlayer.class, origin_column = "playerUUID")
    private UUID playerUUID;
    private int level;
    private String warning;
    private long date;

    private Infraction() {}

    public Infraction(UUID playerUUID, int level, String warning) {
        this.id = ShortUUID.generateUUID(16);
        this.playerUUID = playerUUID;
        this.level = level;
        this.warning = warning;
        this.date = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Infraction warn) {
        return warn.getID().equals(this.id) && warn.getPlayerUUID().equals(this.playerUUID);
    }

    @Override
    public String getID() {
        return id;
    }

}
