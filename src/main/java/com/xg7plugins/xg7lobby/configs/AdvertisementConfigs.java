package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.PlayableSound;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "ads")
public class AdvertisementConfigs extends ConfigSection {

    private boolean enabled;
    private Time cooldown;
    private boolean broadcastOnlyInTheLobby;
    private boolean random;
    private PlayableSound sound;
    private List<Map> advertisements;

}
