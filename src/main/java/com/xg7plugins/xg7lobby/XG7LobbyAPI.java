package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.playerdata.PlayerDataDAO;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerDAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XG7LobbyAPI {

    public static CompletableFuture<LobbyPlayer> requestLobbyPlayer(UUID uuid) {
        return XG7PluginsAPI.getDAO(LobbyPlayerDAO.class).get(uuid);
    }

}
