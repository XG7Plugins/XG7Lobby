package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.xg7lobby.XG7Lobby;

import java.util.UUID;

public class LobbyPlayerDAO implements DAO<UUID, LobbyPlayer> {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Class<LobbyPlayer> getEntityClass() {
        return LobbyPlayer.class;
    }

}
