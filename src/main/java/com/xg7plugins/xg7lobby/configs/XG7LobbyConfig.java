package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.boot.EnvironmentConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class XG7LobbyConfig extends EnvironmentConfig {

    private boolean isChatLocked = false;
    private PlayerConfigs playerConfigs;
    private WorldConfigs worldConfigs;

    public void reloadConfigs() {
        playerConfigs = new PlayerConfigs();
        worldConfigs = new WorldConfigs();
    }

}
