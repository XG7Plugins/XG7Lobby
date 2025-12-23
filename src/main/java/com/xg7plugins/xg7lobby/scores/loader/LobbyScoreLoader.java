package com.xg7plugins.xg7lobby.scores.loader;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7scores.Score;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.ScoreConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Function;

@AllArgsConstructor
public abstract class LobbyScoreLoader {

    @Getter
    protected ScoreConfig scoreConfig;
    @Getter
    protected String scoreID;

    protected final Function<Player, Boolean> condition = player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player);

    public abstract Score load();

}
