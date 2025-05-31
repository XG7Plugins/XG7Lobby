package com.xg7plugins.xg7lobby.lobby.location;

import com.xg7plugins.managers.Manager;
import lombok.Getter;

public class LobbyManager implements Manager {

    @Getter
    private final LobbyLocationDAO lobbyLocationDAO;


    public LobbyManager() {
        this.lobbyLocationDAO = new LobbyLocationDAO();
    }
}
