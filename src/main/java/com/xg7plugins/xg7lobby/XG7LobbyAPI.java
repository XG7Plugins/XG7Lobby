package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.xg7lobby.lobby.location.LobbyManager;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerDAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XG7LobbyAPI {

    public static CompletableFuture<LobbyPlayer> requestLobbyPlayer(UUID uuid) {
        return XG7PluginsAPI.getDAO(LobbyPlayerDAO.class).get(uuid);
    }

    public static LobbyManager lobbyManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), LobbyManager.class);
    }

}
