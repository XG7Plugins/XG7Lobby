package com.xg7plugins.xg7lobby.data.location;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.server.ServerInfo;
import com.xg7plugins.utils.location.Location;
import lombok.Data;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Objects;

@Data
@Table(name = "lobbies")
public class LobbyLocation implements Entity<String, LobbyLocation> {

    @Pkey
    @Column(name = "id", length = 10)
    private final String id;
    private final Location location;
    private final ServerInfo serverInfo;

    public LobbyLocation(String id, Location location, ServerInfo serverInfo) {
        this.id = id;
        this.location = location;
        this.serverInfo = serverInfo;
    }

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

    public void teleport(Player player) {

        Objects.requireNonNull(location, "Location is null");
        Objects.requireNonNull(serverInfo, "ServerInfo is null");

        if (!XG7Plugins.getAPI().getServerInfo().equals(serverInfo)) {
            try {
                serverInfo.connectPlayer(player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        location.teleport(player);

    }


}
