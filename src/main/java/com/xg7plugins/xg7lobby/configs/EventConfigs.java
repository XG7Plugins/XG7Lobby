package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;

public class EventConfigs {

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "events", path = "on-join.")
    public static class OnJoin extends ConfigSection {
        private boolean tpToLobby;
        private String lobbyToTpId;
        private boolean heal;
        private boolean clearInventory;
        private boolean runEventsWhenReturnToTheWorld;
        private boolean sendJoinMessage;
        private boolean sendJoinMessageOnlyOnLobby;
        private List<String> events;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "events", path = "on-first-join.")
    public static class OnFirstJoin extends ConfigSection {
        private boolean enabled;
        private boolean sendFirstJoinMessage;
        private List<String> events;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "events", path = "on-quit.")
    public static class OnQuit extends ConfigSection {
        private boolean enabled;
        private boolean runEventsWhenReturnToTheWorld;
        private boolean sendQuitMessage;
        private boolean sendQuitMessageOnlyOnLobby;
        private List<String> events;
    }
}
