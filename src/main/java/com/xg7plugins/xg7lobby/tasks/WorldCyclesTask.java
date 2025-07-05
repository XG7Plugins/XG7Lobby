package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.configs.WorldConfigs;
import com.xg7plugins.xg7lobby.configs.XG7LobbyEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class WorldCyclesTask extends TimerTask {

    public WorldCyclesTask() {
        super(
                XG7Lobby.getInstance(),
                "world-cycles",
                0,
                Config.of(XG7Lobby.getInstance(), WorldConfigs.class).getWorldTaskDelay().getMilliseconds(),
                TaskState.RUNNING,
                false
        );

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());


    }

    @Override
    public void run() {

        WorldConfigs configs = Config.of(XG7Lobby.getInstance(), WorldConfigs.class);

        Bukkit.getWorlds().stream().filter(world -> XG7PluginsAPI.isEnabledWorld(XG7Lobby.getInstance(), world)).forEach(world -> {

            if (MinecraftVersion.isNewerOrEqual(13)) world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, configs.isDayCycle());
            else world.setGameRuleValue("doDaylightCycle", String.valueOf(configs.isDayCycle()));

            if (!configs.isDayCycle()) {
                world.setTime(translateTimeToMinecraftTicks(configs.getTime()));
            }

            if (MinecraftVersion.isNewerOrEqual(13)) world.setGameRule(GameRule.DO_WEATHER_CYCLE, configs.isWeatherCycle());
            else world.setGameRuleValue("doWeatherCycle", String.valueOf(configs.isWeatherCycle()));

            if (!configs.isWeatherCycle()) world.setStorm(configs.isStorm());

        });


    }

    private long translateTimeToMinecraftTicks(String timeInput) {
        Integer hours = null;
        Integer minutes = null;

        String input = timeInput.trim().toUpperCase().replaceAll("\\s+", " ");

        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("H"),
                DateTimeFormatter.ofPattern("HH"),

                DateTimeFormatter.ofPattern("h:mm a"),
                DateTimeFormatter.ofPattern("hh:mm a"),
                DateTimeFormatter.ofPattern("h a"),
                DateTimeFormatter.ofPattern("hh a"),

                DateTimeFormatter.ofPattern("ha"),
                DateTimeFormatter.ofPattern("h:mma"),
                DateTimeFormatter.ofPattern("hhmma"),
                DateTimeFormatter.ofPattern("hmm a")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalTime time = LocalTime.parse(input, formatter);

                hours = time.getHour();
                minutes = time.getMinute();

            } catch (DateTimeParseException ignored) {
            }
        }

        if (hours == null || minutes == null) throw new IllegalArgumentException("Invalid time format! Use 00:00 - 23:59 or 12 AM - 11:59 PM.");

        int adjustedHours = hours >= 6 ? hours - 6 : hours + 18;

        int ticksFromHour = adjustedHours * 1000;
        int ticksFromMinute = (int) (minutes * (1000.0 / 60));

        long totalTicks = ticksFromHour + ticksFromMinute;

        return totalTicks % 24000;

    }

}
