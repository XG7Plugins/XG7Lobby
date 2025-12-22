package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;

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
                XG7LobbyLoader.getInstance(),
                "anti-spam-tolerance",
                0,
                ConfigFile.mainConfigOf(XG7LobbyLoader.getInstance()).section("anti-spam").getTimeInMilliseconds("time-for-decrement-spam-tolerance"),
                TaskState.RUNNING,
                null
        );
    }

    @Override
    public void run() {
        toleranceCount.replaceAll((uuid, value) -> value - 1);
    }
}
