package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config")
public class MainConfigs extends ConfigSection {

    private boolean buildSystemEnabled;
    private boolean menusEnabled;
    private String mainSelectorId;
    private String mainPvpSelectorId;

    private Time effectsTaskDelay;
    private List<String> effects;


}