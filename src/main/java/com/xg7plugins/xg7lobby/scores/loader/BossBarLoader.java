package com.xg7plugins.xg7lobby.scores.loader;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.modules.xg7scores.builder.BossBarBuilder;
import com.xg7plugins.server.MinecraftServerVersion;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.Collections;

public class BossBarLoader extends LobbyScoreLoader {

    public BossBarLoader() {
        super(new ScoreConfig(ConfigFile.of("scores/scorebar", XG7Lobby.getInstance()).section("boss-bar")),  "xg7lobby-bb");
    }

    @Override
    public Score load() {
        if (!scoreConfig.isEnabled()) return null;

        if (MinecraftServerVersion.isNewerThan(ServerVersion.V_1_8_8)) {
            boolean isPublic = scoreConfig.get("public", Boolean.class).orElse(false);

            BarColor color = scoreConfig.get("color", BarColor.class).orElse(BarColor.WHITE);
            BarStyle style = scoreConfig.get("style", BarStyle.class).orElse(BarStyle.SOLID);

            return BossBarBuilder.bossBar("xg7lobby-bb")
                    .title(scoreConfig.getList("title", String.class).orElse(Collections.emptyList()))
                    .progress(scoreConfig.get("progress", Float.class).orElse(100f))
                    .color(color)
                    .style(style)
                    .isPublic(isPublic)
                    .delay(scoreConfig.getDelay())
                    .condition(condition)
                    .build(XG7Lobby.getInstance());
        }

        return BossBarBuilder.bossBar(scoreID)
                .title(scoreConfig.getList("title", String.class).orElse(Collections.emptyList()))
                .progress(scoreConfig.get("progress", Float.class).orElse(100f))
                .delay(scoreConfig.getDelay())
                .condition(condition)
                .build(XG7Lobby.getInstance());
    }

}
