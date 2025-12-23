package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.PlayableSound;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoBroadcastTask extends TimerTask {

    private final ConfigSection config = ConfigFile.of("ads", XG7Lobby.getInstance()).root();

    private final AtomicInteger index = new AtomicInteger(0);

    public AutoBroadcastTask() {
        super(
                XG7Lobby.getInstance(),
                "auto-broadcast",
                0,
                ConfigFile.of("ads", XG7Lobby.getInstance()).root().getTimeInMilliseconds("cooldown"),
                TaskState.RUNNING,
                false
        );

    }

    @Override
    public void run() {

        List<Map> advertisements = config.getList("advertisements", Map.class).orElse(Collections.emptyList());

        if (advertisements.isEmpty()) return;

        int index = config.get("random", false) ? new Random().nextInt(advertisements.size()) : this.index.getAndIncrement();

        if (index >= advertisements.size()) {
            this.index.set(1);
            index = 0;
        }

        int finalIndex = index;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (config.get("broadcast-only-in-the-lobby", false) && !XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

            ((List<String>) advertisements.get(finalIndex).get("ad")).forEach(message -> Text.detectLangs(
                    player,
                    XG7Lobby.getInstance(),
                    message
            ).join().send(player));

            PlayableSound sound = config.get("sound", PlayableSound.class);

            if (sound != null) sound.play(player);
        });


    }



}