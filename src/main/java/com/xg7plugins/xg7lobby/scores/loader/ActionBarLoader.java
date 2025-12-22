package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.ActionBarBuilder;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.ArrayList;

public class ActionBarLoader extends LobbyScoreLoader {
    public ActionBarLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/scorebar", XG7LobbyLoader.getInstance()).section("action-bar")), "xg7lobby-ab");
    }

    @Override
    public Score load() {

        if (!scoreConfig.isEnabled()) return null;

        return ActionBarBuilder.actionBar(scoreID)
                .text(scoreConfig.getList("text", String.class).orElse(new ArrayList<>()))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .build(XG7LobbyLoader.getInstance());
    }
}
