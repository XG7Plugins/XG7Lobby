package com.xg7plugins.xg7lobby.data.player.dao;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.database.dao.DAO;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.Infraction;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
