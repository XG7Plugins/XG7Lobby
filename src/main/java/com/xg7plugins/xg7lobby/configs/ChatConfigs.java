package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigField;
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

    @ConfigField(name = "anti-swearing.enabled")
    private boolean antiSwearEnabled;
    @ConfigField(name = "anti-swearing.blockedWords")
    private List<String> blockedWords;
    @ConfigField(name = "anti-swearing.replacement")
    private String chatReplacement;
    @ConfigField(name = "anti-swearing.dont-send-the-message")
    private boolean dontSendTheSwearMessage;
    @ConfigField(name = "anti-swearing.anti-swear-only-on-lobby")
    private boolean antiSwearOnlyOnLobby;

    @ConfigField(name = "block-commands.enabled")
    private boolean blockCommandsEnabled;
    @ConfigField(name = "block-commands.anti-tab")
    private boolean antiTabEnabled;
    @ConfigField(name = "block-commands.blocked-commands")
    private List<String> blockedCommands;

    @ConfigField(name = "anti-spam.enabled")
    private boolean antiSpamEnabled;

    @ConfigField(name = "anti-spam.anti-spam-only-on-lobby")
    private boolean antiSpamOnlyOnLobby;
    @ConfigField(name = "anti-spam.message-cannot-be-the-same")
    private boolean messageCannotBeTheSame;
    @ConfigField(name = "anti-spam.cooldown")
    private Time cooldown;
    @ConfigField(name = "anti-spam.spam-tolerance")
    private int spamTolerance;
    @ConfigField(name = "anti-spam.time-for-decrement-spam-tolerance")
    private Time timeForDecrementSpamTolerance;
    @ConfigField(name = "anti-spam.send-warning-on-message")
    private int sendWarningOnMessage;
    @ConfigField(name = "anti-spam.mute-on-spam-limit")
    private boolean muteOnSpamLimit;
    @ConfigField(name = "anti-spam.unmute-delay")
    private Time unmuteDelay;
    @ConfigField(name = "anti-spam.spam-warn-level")
    private int spamWarnLevel;

}
