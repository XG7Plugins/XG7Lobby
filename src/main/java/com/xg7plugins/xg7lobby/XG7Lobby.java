package com.xg7plugins.xg7lobby;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.dependencies.Dependency;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.xg7lobby.lobby.location.LobbyLocationDAO;
import com.xg7plugins.xg7lobby.lobby.location.LobbyManager;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerDAO;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@PluginSetup(
    prefix = "§1X§9G§37§bLobby§r",
        onEnableDraw = {
                "§1 __   §9_______ §3______ §b_           _     _           ",
                "§1 \\ \\ §9/ / ____§3|____  §b| |         | |   | |          ",
                "§1  \\ V §9/ |  __ §3   / /§b| |     ___ | |__ | |__  _   _ ",
                "§1   > <§9| | |_ |  §3/ / §b| |    / _ \\| '_ \\| '_ \\| | | |",
                "§1  / . §9\\ |__| | §3/ /  §b| |___| (_) | |_) | |_) | |_| |",
                "§1 /_/ \\_§9\\_____|§3/_/   §b|______\\___/|_.__/|_.__/ \\__, |",
                "§b                                              __/ |",
                "§b                                             |___/ "

        },
        mainCommandName = "xg7lobby",
        mainCommandAliases = {"7l", "xg7l"},
        configs = {"ads", "custom-commands", "events", "pvp", "scores"},
        reloadCauses = {"scores", "actions", "menus"}

)
public final class XG7Lobby extends Plugin {

    @Override
    public void onLoad() {
        this.environmentConfig = new XG7LobbyConfig();
        super.onLoad();
    }

    @Override
    public void onEnable() {

        managerRegistry.registerManager(new LobbyManager());

    }


    public static XG7Lobby getInstance() {
        return getPlugin(XG7Lobby.class);
    }

    @Override
    public Class<? extends Entity<?,?>>[] loadEntities() {
        return new Class[]{LobbyPlayer.class};
    }

    @Override
    public List<DAO<?,?>> loadDAOs() {
        return Arrays.asList(new LobbyPlayerDAO(), new LobbyLocationDAO());
    }

    @Override
    public List<Command> loadCommands() {
        return null;
    }

    @Override
    public List<Listener> loadEvents() {
        return null;
    }

    @Override
    public List<PacketListener> loadPacketEvents() {
        return null;
    }

    @Override
    public List<Task> loadRepeatingTasks() {
        return null;
    }

    @Override
    public void loadHelp() {

    }
}
