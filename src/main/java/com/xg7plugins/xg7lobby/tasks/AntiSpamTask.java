package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;

import java.util.HashMap;
import java.util.UUID;

public class AntiSpamTask extends TimerTask {

    private final HashMap<UUID, Integer> toleranceCount = new HashMap<>();

    public void incrementTolerance(UUID uuid) {
        toleranceCount.putIfAbsent(uuid, 0);
        toleranceCount.put(uuid, toleranceCount.get(uuid) + 1);
    }
    public void decrementTolerance(UUID uuid) {
        toleranceCount.putIfAbsent(uuid, 0);
        toleranceCount.put(uuid, toleranceCount.get(uuid) - 1);
    }

    public int getTolerance(UUID uuid) {
        return toleranceCount.get(uuid);
    }


    public AntiSpamTask() {
        super(
                XG7Lobby.getInstance(),
                "anti-spam-tolerance",
                0,
                Config.of(XG7Lobby.getInstance(), ChatConfigs.AntiSpam.class).getTimeForDecrementSpamTolerance().getMilliseconds(),
                TaskState.RUNNING,
                null
        );
    }

    @Override
    public void run() {
        toleranceCount.replaceAll((uuid, value) -> value - 1);
    }
}
