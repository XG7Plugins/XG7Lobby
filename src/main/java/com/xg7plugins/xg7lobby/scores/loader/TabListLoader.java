package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.TablistBuilder;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabListLoader extends LobbyScoreLoader {
    public TabListLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/tablist", XG7LobbyLoader.getInstance()).section("tab-list")), "xg7lobby-tb");
    }

    @Override
    public Score load() {
        if (!scoreConfig.isEnabled()) return null;

        List<List<String>> headerList = scoreConfig.getList("headers", Map.class).orElse(new ArrayList<>()).stream().map(map -> (List<String>) map.get("state")).collect(Collectors.toList());

        List<List<String>> footerList = scoreConfig.getList("footers", Map.class).orElse(new ArrayList<>()).stream().map(map -> (List<String>) map.get("state")).collect(Collectors.toList());

        return TablistBuilder.tablist(scoreID)
                .header(headerList.stream().map(list -> String.join("\n", list)).collect(Collectors.toList()))
                .footer(footerList.stream().map(list -> String.join("\n", list)).collect(Collectors.toList()))
                .playerPrefix(scoreConfig.get("custom-player-prefix", String.class).orElse(""))
                .playerSuffix(scoreConfig.get("custom-player-suffix", String.class).orElse(""))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .build(XG7LobbyLoader.getInstance());
    }
}
