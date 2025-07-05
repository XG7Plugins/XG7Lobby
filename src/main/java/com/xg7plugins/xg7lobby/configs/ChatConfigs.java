package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config")
public class ChatConfigs extends ConfigSection {

    private boolean lockChatOnlyOnLobby;

    public AntiSwearing getAntiSwearingConfigs() {
        return Config.of(XG7Lobby.getInstance(), AntiSwearing.class);
    }
    public BlockCommands getBlockCommandsConfigs() {
        return Config.of(XG7Lobby.getInstance(), BlockCommands.class);
    }
    public AntiSpam getAntiSpamConfigs() {
        return Config.of(XG7Lobby.getInstance(), AntiSpam.class);
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "anti-swearing.")
    public static class AntiSwearing extends ConfigSection {
        private boolean enabled;
        private List<String> blockedWords;
        private String replacement;
        private boolean dontSendTheMessage;
        private boolean antiSwearOnlyOnLobby;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "block-commands.")
    public static class BlockCommands extends ConfigSection {
        private boolean enabled;
        private boolean antiTab;
        private List<String> blockedCommands;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "config", path = "anti-spam.")
    public static class AntiSpam extends ConfigSection {
        private boolean enabled;
        private boolean antiSpamOnlyOnLobby;
        private boolean messageCannotBeTheSame;
        private Time cooldown;
        private int spamTolerance;
        private Time timeForDecrementSpamTolerance;
        private int sendWarningOnMessage;
        private boolean muteOnSpamLimit;
        private Time unmuteDelay;
        private int spamWarnLevel;
    }

}
