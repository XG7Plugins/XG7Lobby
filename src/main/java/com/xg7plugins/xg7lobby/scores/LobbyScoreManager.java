package com.xg7plugins.xg7lobby.scores;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.modules.xg7scores.XG7Scores;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.scores.loader.*;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class LobbyScoreManager implements Manager {

    private final List<LobbyScoreLoader> lobbyScoreLoaders = new ArrayList<>();

    public LobbyScoreManager() {
        lobbyScoreLoaders.add(new SidebarLoader());
        lobbyScoreLoaders.add(new TabListLoader());
        lobbyScoreLoaders.add(new BossBarLoader());
        lobbyScoreLoaders.add(new XPBarLoader());
        lobbyScoreLoaders.add(new ActionBarLoader());
        lobbyScoreLoaders.add(new BelowNameIndicatorLoader());
        lobbyScoreLoaders.add(new TabListScoreLoader());
    }


    public void loadScores() {
        Debug.of(XG7Lobby.getInstance()).info("Loading lobby scores");

        lobbyScoreLoaders.forEach((loader) -> {
            loader.getScoreConfig().load();
            if (!loader.getScoreConfig().isEnabled()) return;

            Debug.of(XG7Lobby.getInstance()).info("Loading " + loader.getClass().getSimpleName());

            XG7PluginsAPI.taskManager().runTimerTask(XG7PluginsAPI.taskManager().getTimerTask(XG7Plugins.getInstance(), "score-task"));
            XG7Scores.loadScores(loader.load());
            Bukkit.getOnlinePlayers().stream().filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p)).forEach(XG7Scores.getInstance()::addPlayer);

            Debug.of(XG7Lobby.getInstance()).info("Loaded " + loader.getScoreID());
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
