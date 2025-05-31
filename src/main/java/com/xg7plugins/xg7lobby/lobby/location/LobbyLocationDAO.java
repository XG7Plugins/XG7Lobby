package com.xg7plugins.xg7lobby.lobby.location;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.xg7lobby.XG7Lobby;

public class LobbyLocationDAO implements DAO<String, LobbyLocation> {
    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Class<LobbyLocation> getEntityClass() {
        return LobbyLocation.class;
    }
}
