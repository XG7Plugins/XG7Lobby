package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;

public class WorldConfigs {

    public final Time worldTaskDelay;

    public final boolean weatherCycle;
    public final boolean storm;

    public final boolean dayCycle;
    public final String time;

    public final boolean spawnMobs;
    public final boolean leavesDecay;
    public final boolean burnBlocks;
    public final boolean blockSpread;

    public final boolean cancelExplosions;

    public WorldConfigs() {
        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        this.worldTaskDelay = config.getTime("world-task-delay").orElse(Time.of(10));

        this.weatherCycle = config.get("weather-cycle", Boolean.class).orElse(false);
        this.storm = config.get("storm", Boolean.class).orElse(false);

        this.dayCycle = config.get("day-cycle", Boolean.class).orElse(false);
        this.time = config.get("time", String.class).orElse("12:30");

        this.spawnMobs = config.get("spawn-mobs", Boolean.class).orElse(false);
        this.leavesDecay = config.get("leaves-decay", Boolean.class).orElse(false);
        this.burnBlocks = config.get("burn-blocks", Boolean.class).orElse(false);
        this.blockSpread = config.get("block-spread", Boolean.class).orElse(false);

        this.cancelExplosions = config.get("cancel-explosions", Boolean.class).orElse(true);
    }

}
