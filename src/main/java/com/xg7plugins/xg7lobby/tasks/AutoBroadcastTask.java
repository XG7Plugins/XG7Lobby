package com.xg7plugins.xg7lobby.tasks;

import com.cryptomorin.xseries.XSound;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoBroadcastTask extends TimerTask {

    private final AtomicInteger index = new AtomicInteger(0);

    private final Sound sound;

    private final float volume;
    private final float pitch;

    private final List<Map> ads;

    private final boolean broadcastOnlyInLobby;

    private final boolean random;

    public AutoBroadcastTask() {
        super(
                XG7Lobby.getInstance(),
                "auto-broadcast",
                0,
                Config.of("ads", XG7Lobby.getInstance()).getTime("cooldown").orElse(Time.of(3, 0)).getMilliseconds(),
                TaskState.RUNNING,
                null
        );

        Config config = Config.of("ads", XG7Lobby.getInstance());

        String[] soundString = config.get("sound", String.class).orElse("ENTITY_EXPERIENCE_ORB_PICKUP, 5, 0").split(", ");

        sound = XSound.of(soundString[0]).orElse(XSound.ENTITY_EXPERIENCE_ORB_PICKUP).get();

        volume = Float.parseFloat(soundString[1]);
        pitch = Float.parseFloat(soundString[2]);

        broadcastOnlyInLobby = config.get("broadcast-only-in-the-lobby", Boolean.class).orElse(false);

        random = config.get("random", Boolean.class).orElse(false);

        ads = config.getList("advertisements", Map.class).orElse(null);

    }

    @Override
    public void run() {

        if (ads == null) return;

        int index = random ? new Random().nextInt(ads.size()) : this.index.getAndIncrement();

        if (index >= ads.size()) {
            this.index.set(1);
            index = 0;
        }

        int finalIndex = index;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (broadcastOnlyInLobby && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

            ((List<String>)ads.get(finalIndex).get("ad")).forEach(message -> Text.detectLangs(
                    player,
                    XG7Lobby.getInstance(),
                    message
            ).join().send(player));

            player.playSound(player.getLocation(), sound, volume, pitch);
        });


    }



}