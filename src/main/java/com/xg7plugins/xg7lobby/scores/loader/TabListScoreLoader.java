package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.ScoreBuilder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

public class TabListScoreLoader extends LobbyScoreLoader {
    public TabListScoreLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/tablist", XG7Lobby.getInstance()).section("tab-list-score")), "xg7lobby-tl");
    }

    @Override
    public Score load() {

        if (!scoreConfig.isEnabled()) return null;

        return ScoreBuilder.tablistScore(scoreID)
                .condition(condition)
                .delay(hashCode())
                .integerValuePlaceholder(scoreConfig.get("score", String.class).orElse("0"))
                .build(XG7Lobby.getInstance());
    }
}
