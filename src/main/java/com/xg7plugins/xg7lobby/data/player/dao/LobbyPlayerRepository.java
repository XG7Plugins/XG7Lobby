package com.xg7plugins.xg7lobby.data.player.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;

import java.util.UUID;

public class LobbyPlayerRepository implements Repository<UUID, LobbyPlayer> {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Class<LobbyPlayer> getEntityClass() {
        return LobbyPlayer.class;
    }

}
