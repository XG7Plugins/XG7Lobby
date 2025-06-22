package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.ScoreBoardBuilder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.ArrayList;

public class ScoreBoardLoader extends LobbyScoreLoader {

    private final ScoreConfig belowNameConfig;

    public ScoreBoardLoader() {
        super(new ScoreConfig("scoreboard"), "xg7lobby-sb");

        this.belowNameConfig = new ScoreConfig("health-display");
    }

    @Override
    public Score load() {
        if (!belowNameConfig.isEnabled() && !scoreConfig.isEnabled()) return null;

        if (belowNameConfig.isEnabled() && scoreConfig.isEnabled()) return loadBoth().build(XG7Lobby.getInstance());

        if (!belowNameConfig.isEnabled()) return loadScore().build(XG7Lobby.getInstance());

        return loadHealthDisplay().build(XG7Lobby.getInstance());
    }

    private ScoreBoardBuilder loadScore() {
        return ScoreBoardBuilder.scoreBoard(scoreID)
                .title(scoreConfig.getList("title", String.class).orElse(new ArrayList<>()))
                .lines(scoreConfig.getList("lines", String.class).orElse(new ArrayList<>()))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .allowSideBar(true)
                .allowHealthDisplay(false);
    }

    private ScoreBoardBuilder loadHealthDisplay() {
        return ScoreBoardBuilder.scoreBoard(scoreID)
                .allowHealthDisplay(true)
                .allowSideBar(false)
                .healthDisplaySuffix(belowNameConfig.get("suffix", String.class).orElse("HEALTH"))
                .delay(belowNameConfig.getDelay())
                .condition(condition);
    }

    private ScoreBoardBuilder loadBoth() {
        return loadScore()
                .allowHealthDisplay(true)
                .healthDisplaySuffix(belowNameConfig.get("suffix", String.class).orElse("HEALTH"));
    }
}
