package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config")
public class WorldConfigs extends ConfigSection {

    public Time worldTaskDelay;

    private boolean weatherCycle;
    private boolean storm;

    private boolean dayCycle;
    private String time;

    private boolean spawnMobs;
    private boolean leavesDecay;
    private boolean burnBlocks;
    private boolean blockSpread;

    private boolean cancelExplosions;

}
