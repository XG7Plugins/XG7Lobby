package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;

public class ForeverActionsTask extends TimerTask {

    public ForeverActionsTask() {
        super(
                XG7Lobby.getInstance(),
                "effects",
                0,
                ConfigFile.of("events", XG7Lobby.getInstance()).section("repeat-forever").getTimeInMilliseconds("delay-for-repeat"),
                TaskState.RUNNING,
                false
        );

    }

    @Override
    public void run() {

        ConfigSection config = ConfigFile.of("events", XG7Lobby.getInstance()).section("repeat-forever");

        List<String> actions = config.getList("actions", String.class).orElse(Collections.emptyList());

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), p)).
                forEach(player -> ActionsProcessor.process(actions, player));
    }
}
