package com.xg7plugins.xg7lobby.scores;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class ScoreConfig {

    private final String scorePath;
    private final boolean enabled;
    private final long delay;

    public ScoreConfig (String scorePath) {
        this.scorePath = scorePath;
        Config config = Config.of("scores", XG7Lobby.getInstance());
        this.enabled = config.get(scorePath + ".enabled", Boolean.class).orElse(false);
        this.delay = config.getTime(scorePath + ".update-time").orElse(Time.of(30)).getMilliseconds();
    }

    public <T> Optional<T> get(String path, Class<T> clazz) {
        return Config.of("scores", XG7Lobby.getInstance()).get(scorePath + "." + path,  clazz);
    }
    public <T> Optional<List<T>> getList(String path, Class<T> clazz) {
        return Config.of("scores", XG7Lobby.getInstance()).getList(scorePath + "." + path,  clazz);
    }

}
