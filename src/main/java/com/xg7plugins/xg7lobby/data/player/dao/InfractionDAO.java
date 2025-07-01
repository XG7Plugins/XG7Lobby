package com.xg7plugins.xg7lobby.data.player.dao;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.DAO;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;

import java.util.UUID;

public class InfractionDAO implements DAO<String, Infraction> {

    @Override
    public Plugin getPlugin() {
        return XG7Lobby.getInstance();
    }

    @Override
    public Class<Infraction> getEntityClass() {
        return Infraction.class;
    }

}

