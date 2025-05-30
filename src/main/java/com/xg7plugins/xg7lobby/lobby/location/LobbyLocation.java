package com.xg7plugins.xg7lobby.lobby.location;

import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.utils.location.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LobbyLocation implements Entity<String, LobbyLocation> {

    private final String id;
    private final Location location;
    private final ServerInfo serverInfo;


    private LobbyLocation() {
        id = null;
        location = null;
        serverInfo = null;
    }

    @Override
    public boolean equals(LobbyLocation other) {
        return id.equals(other.getID()) && location.equals(other.getLocation()) && serverInfo.equals(other.getServerInfo());
    }

    @Override
    public String getID() {
        return id;
    }

    public void teleport(Play)


}
