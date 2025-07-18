package com.xg7plugins.xg7lobby.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.loader.*;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class LobbyScoreManager implements Manager {

    private final List<LobbyScoreLoader> lobbyScoreLoaders = new ArrayList<>();

    public LobbyScoreManager() {
        lobbyScoreLoaders.add(new ScoreBoardLoader());
        lobbyScoreLoaders.add(new TabListLoader());
        lobbyScoreLoaders.add(new BossBarLoader());
        lobbyScoreLoaders.add(new XPBarLoader());
        lobbyScoreLoaders.add(new ActionBarLoader());
    }


    public void loadScores() {
        lobbyScoreLoaders.forEach((loader) -> {
            loader.getScoreConfig().load();
            if (!loader.getScoreConfig().isEnabled()) return;
            XG7PluginsAPI.taskManager().runTimerTask(XG7PluginsAPI.taskManager().getTimerTask(XG7Plugins.getInstance(), "score-task"));
            XG7Scores.loadScores(loader.load());
            Bukkit.getOnlinePlayers().stream().filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p)).forEach(XG7Scores.getInstance()::addPlayer);
        });
    }

    public void unloadScores() {

        Bukkit.getOnlinePlayers().stream().filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p)).forEach(XG7Scores.getInstance()::removePlayer);
        lobbyScoreLoaders.forEach((loader) -> XG7Scores.unloadScore(loader.getScoreID()));
    }

    public void reloadScores() {
        unloadScores();
        loadScores();
    }

}
