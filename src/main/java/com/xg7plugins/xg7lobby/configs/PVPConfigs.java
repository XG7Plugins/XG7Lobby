package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "pvp")
public class PVPConfigs extends ConfigSection {

    private boolean enabled;
    private Time leaveCommandCooldown;
    private boolean hidePlayersNotInPvp;
    private boolean disableMultiJumps;
    private boolean disableLaunchpad;
    private boolean disableLobbyEffects;
    private boolean foodChange;
    private boolean dropsEnabled;
    private Time combatLogRemove;
    private String pvpLobby;
    private List<String> commandsBlocked;

    public OnJoinPvp getOnJoinConfigs() {
        return Config.of(XG7Lobby.getInstance(), OnJoinPvp.class);
    }
    public OnKill getOnKillConfigs() {
        return Config.of(XG7Lobby.getInstance(), OnKill.class);
    }
    public OnRespawn getOnRespawnConfigs() {
        return Config.of(XG7Lobby.getInstance(), OnRespawn.class);
    }
    public OnLeavePvp getOnLeaveConfigs() {
        return Config.of(XG7Lobby.getInstance(), OnLeavePvp.class);
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "pvp", path = "on-join-pvp")
    public static class OnJoinPvp extends ConfigSection {
        private boolean heal;
        private boolean teleportToPvpLobby;
        private List<String> actions;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "pvp", path = "on-kill")
    public static class OnKill extends ConfigSection {
        private List<String> killerActions;
        private List<String> victimActions;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "pvp", path = "on-respawn")
    public static class OnRespawn extends ConfigSection {
        private boolean teleportToPvpLobby;
        private boolean resetItems;
        private List<String> actions;
    }

    @Getter
    @ConfigFile(plugin = XG7Lobby.class, configName = "pvp", path = "on-leave-pvp")
    public static class OnLeavePvp extends ConfigSection {
        private boolean dontMove;
        private List<String> actions;
    }
}
