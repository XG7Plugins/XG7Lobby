package com.xg7plugins.xg7lobby.events.lobby_events;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.event.server.ServerListPingEvent;

public class MOTDListener implements Listener {
    @Override
    public boolean isEnabled() {
        return Config.mainConfigOf(XG7Lobby.getInstance()).get("motd.enabled", Boolean.class).orElse(false);
    }

    public void onServerPing(ServerListPingEvent event) {

        Config config =  Config.mainConfigOf(XG7Lobby.getInstance());




    }

}

