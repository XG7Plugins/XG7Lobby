package com.xg7plugins.xg7lobby.data.location;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.xg7lobby.XG7Lobby;

public class LobbyLocationRepository implements Repository<String, LobbyLocation> {
    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Class<LobbyLocation> getEntityClass() {
        return LobbyLocation.class;
    }
}
