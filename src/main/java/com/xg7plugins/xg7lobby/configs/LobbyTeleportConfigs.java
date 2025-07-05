package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "lobby-teleport-cooldown.")
public class LobbyTeleportConfigs extends ConfigSection {
    private Time beforeTeleport;
    private boolean dontMove;
    private Time afterTeleport;
}
