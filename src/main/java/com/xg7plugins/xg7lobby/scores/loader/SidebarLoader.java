package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.SidebarBuilder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.ArrayList;

public class SidebarLoader extends LobbyScoreLoader {


    public SidebarLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/scoreboard", XG7Lobby.getInstance()).section("sidebar")), "xg7lobby-sb");
    }

    @Override
    public Score load() {
        if (!scoreConfig.isEnabled()) return null;

        return (Score) SidebarBuilder.sidebar(scoreID)
                .title(scoreConfig.getList("title", String.class).orElse(new ArrayList<>()))
                .lines(scoreConfig.getList("lines", String.class).orElse(new ArrayList<>()))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .build(XG7Lobby.getInstance());
    }
}
