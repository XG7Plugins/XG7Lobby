package com.xg7plugins.xg7lobby.tasks;

import com.cryptomorin.xseries.XSound;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.AdvertisementConfigs;
import com.xg7plugins.xg7lobby.configs.ChatConfigs;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoBroadcastTask extends TimerTask {

    private final AtomicInteger index = new AtomicInteger(0);

    private final AdvertisementConfigs config;

    public AutoBroadcastTask() {
        super(
                XG7Lobby.getInstance(),
                "auto-broadcast",
                0,
                Config.of(XG7Lobby.getInstance(), AdvertisementConfigs.class).getCooldown().getMilliseconds(),
                TaskState.RUNNING,
                false
        );

        this.config = Config.of(XG7Lobby.getInstance(), AdvertisementConfigs.class);

    }

    @Override
    public void run() {

        if (config.getAdvertisements() == null || config.getAdvertisements().isEmpty()) return;

        int index = config.isRandom() ? new Random().nextInt(config.getAdvertisements().size()) : this.index.getAndIncrement();

        if (index >= config.getAdvertisements().size()) {
            this.index.set(1);
            index = 0;
        }

        int finalIndex = index;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (config.isBroadcastOnlyInTheLobby() && !XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

            ((List<String>) config.getAdvertisements().get(finalIndex).get("ad")).forEach(message -> Text.detectLangs(
                    player,
                    XG7Lobby.getInstance(),
                    message
            ).join().send(player));

            config.getSound().play(player);
        });


    }



}