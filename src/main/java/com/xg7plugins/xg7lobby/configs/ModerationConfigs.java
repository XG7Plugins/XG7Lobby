package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config")
public class ModerationConfigs extends ConfigSection {

    private boolean warnAdmin;
    private boolean muteAdmin;
    private boolean kickAdmin;
    private boolean banAdmin;
    private List<Map> infractionLevels;
    private int muteWarningLevel;
    private int kickWarningLevel;
    private int banWarningLevel;
    private int totalInfractionsToBan;
    private int totalInfractionsToMute;
    private int totalInfractionsToKick;
    private String infractionsTimeToUnmute;
    private boolean infractionsBanByIp;
}
