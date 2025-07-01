package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AntiSpamTask extends TimerTask {

    private HashMap<UUID, Integer> toleranceCount = new HashMap<>();

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
                Config.mainConfigOf(XG7Lobby.getInstance()).getTime("anti-spam.time-for-decrement-spam-tolerance").orElse(Time.of(1, 0)).getMilliseconds(),
                TaskState.RUNNING,
                null
        );
    }

    @Override
    public void run() {
        toleranceCount.replaceAll((uuid, value) -> value - 1);
    }
}
