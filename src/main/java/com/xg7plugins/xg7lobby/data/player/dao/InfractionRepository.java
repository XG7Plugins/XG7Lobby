package com.xg7plugins.xg7lobby.data.player.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.data.player.Infraction;

public class InfractionRepository implements Repository<String, Infraction> {

    @Override
    public Plugin getPlugin() {
        return XG7LobbyLoader.getInstance();
    }

    @Override
    public Class<Infraction> getEntityClass() {
        return Infraction.class;
    }

}

