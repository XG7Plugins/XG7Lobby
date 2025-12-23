package com.xg7plugins.xg7lobby.scores;


import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class ScoreConfig {

    private ConfigSection scoreSection;
    private boolean enabled;
    private long delay;

    public ScoreConfig (ConfigSection scoreSection) {
        this.scoreSection = scoreSection;
    }

    public <T> Optional<T> get(String path, Class<T> clazz) {
        return Optional.ofNullable(scoreSection.get(path, clazz));
    }
    public <T> Optional<List<T>> getList(String path, Class<T> clazz) {
        return scoreSection.getList(path, clazz);
    }

    public void load() {
        ConfigFile newFile = ConfigFile.of(scoreSection.getFile().getName(), XG7Lobby.getInstance());
        this.scoreSection = new ConfigSection(newFile, scoreSection.getPath(), newFile.getConfig());
        this.enabled = scoreSection.get("enabled",false);
        this.delay = scoreSection.getTimeInMilliseconds("update-time", 30000L);
    }

}
