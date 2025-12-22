package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.XPBarBuilder;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.Collections;

public class XPBarLoader extends LobbyScoreLoader {

    public XPBarLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/scorebar", XG7LobbyLoader.getInstance()).section("xp-bar")), "xg7lobby-xp");
    }

    @Override
    public Score load() {
        if (!scoreConfig.isEnabled()) return null;
        return XPBarBuilder.XPBar(scoreID)
                .setLevels(scoreConfig.getList("levels", String.class).orElse(Collections.singletonList("1 , 0.4")))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .build(XG7LobbyLoader.getInstance());
    }

}
