package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.ScoreBuilder;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.Collections;

public class BelowNameIndicatorLoader extends LobbyScoreLoader {

    public BelowNameIndicatorLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/scoreboard", XG7LobbyLoader.getInstance()).section("below-name-display")), "xg7lobby-bn");
    }

    @Override
    public Score load() {

        if (!scoreConfig.isEnabled()) return null;

        return ScoreBuilder.belowName(scoreID)
                .condition(condition)
                .delay(scoreConfig.getDelay())
                .healthIndicator(scoreConfig.getList("suffixes", String.class).orElse(Collections.singletonList("❤")))
                .integerValuePlaceholder(scoreConfig.get("indicator", String.class).orElse("0"))
                .build(XG7LobbyLoader.getInstance());
    }

}
