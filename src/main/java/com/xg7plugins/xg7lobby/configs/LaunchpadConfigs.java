package com.xg7plugins.xg7lobby.configs;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.PlayableSound;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "launchpad.")
public class LaunchpadConfigs extends ConfigSection {

    private boolean enabled;
    private PlayableSound sound;
    private XMaterial topBlock;
    private XMaterial bottomBlock;
    private int power;
    private int height;
}
