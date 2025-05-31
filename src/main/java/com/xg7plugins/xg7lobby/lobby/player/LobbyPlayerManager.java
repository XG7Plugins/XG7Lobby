package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.managers.Manager;
import lombok.Getter;

public class LobbyPlayerManager implements Manager {

    @Getter
    private final LobbyPlayerDAO lobbyPlayerDAO;

    public LobbyPlayerManager() {
        this.lobbyPlayerDAO = new LobbyPlayerDAO();
    }





}
