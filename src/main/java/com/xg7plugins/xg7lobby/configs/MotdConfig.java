package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "motd.")
public class MotdConfig extends ConfigSection {

    private boolean enabled;
    private int maxPlayers;
    private List<String> text;
}
