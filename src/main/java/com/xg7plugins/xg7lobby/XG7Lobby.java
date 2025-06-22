package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.commands.core_commands.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.Task;
import com.xg7plugins.xg7lobby.commands.lobby.DeleteLobby;
import com.xg7plugins.xg7lobby.commands.lobby.Lobbies;
import com.xg7plugins.xg7lobby.commands.lobby.Lobby;
import com.xg7plugins.xg7lobby.commands.lobby.SetLobby;
import com.xg7plugins.xg7lobby.commands.toggle_commands.BuildCommand;
import com.xg7plugins.xg7lobby.commands.toggle_commands.FlyCommand;
import com.xg7plugins.xg7lobby.commands.toggle_commands.VanishCommand;
import com.xg7plugins.xg7lobby.commands.utils.ExecuteActionCommand;
import com.xg7plugins.xg7lobby.commands.utils.GamemodeCommand;
import com.xg7plugins.xg7lobby.commands.utils.OpenInventoryCommand;
import com.xg7plugins.xg7lobby.configs.XG7LobbyConfig;
import com.xg7plugins.xg7lobby.events.air_events.LaunchpadListener;
import com.xg7plugins.xg7lobby.events.air_events.MultiJumpingListener;
import com.xg7plugins.xg7lobby.events.command_events.LobbyCommandEvent;
import com.xg7plugins.xg7lobby.events.lobby_events.DefaultPlayerEvents;
import com.xg7plugins.xg7lobby.events.lobby_events.DefaultWorldEvents;
import com.xg7plugins.xg7lobby.events.lobby_events.LoginAndLogoutEvent;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyGUITypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyHotbarTypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyItemTypeAdapter;
import com.xg7plugins.xg7lobby.menus.default_menus.LobbiesMenu;
import com.xg7plugins.xg7lobby.lobby.location.LobbyLocation;
import com.xg7plugins.xg7lobby.lobby.location.LobbyManager;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.scores.LobbyScoreManager;
import com.xg7plugins.xg7lobby.tasks.AutoBroadcastTask;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
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
        reloadCauses = {"scores", "menus"}

)
public final class XG7Lobby extends Plugin {

    @Override
    public void onLoad() {
        this.environmentConfig = new XG7LobbyConfig();
        super.onLoad();
        ((XG7LobbyConfig)environmentConfig).reloadConfigs();
    }

    @Override
    public void onEnable() {

        super.onEnable();

        debug.loading("Loading managers...");

        managerRegistry.registerManagers(
                new LobbyPlayerManager(),
                new LobbyManager(),
                new LobbyScoreManager()
        );

        if (Config.mainConfigOf(this).get("menus-enabled", Boolean.class).orElse(false)) {
            managerRegistry.registerManager(new CustomInventoryManager());
        }

        debug.loading("Loading scores...");

        loadScores();

        debug.loading("Loading menus...");

        loadMenus();

        debug.loading("XG7Lobby enabled.");
    }

    @Override
    public void onReload(ReloadCause cause) {
        if (cause.equals("scores")) {
            debug.loading("Reloading scores...");
            LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
            lobbyScoreManager.reloadScores();
            debug.loading("Scores reloaded.");
        }
        if (cause.equals("menus")) {
            debug.loading("Reloading menus...");
            CustomInventoryManager inventoryManager = XG7LobbyAPI.customInventoryManager();
            inventoryManager.reloadInventories();
            debug.loading("Menus reloaded.");
        }

    }

    public static XG7Lobby getInstance() {
        return getPlugin(XG7Lobby.class);
    }

    @Override
    public Class<? extends Entity<?,?>>[] loadEntities() {
        return new Class[]{LobbyPlayer.class, LobbyLocation.class};
    }

    @Override
    public List<DAO<?,?>> loadDAOs() {
        return Arrays.asList(XG7LobbyAPI.lobbyManager().getLobbyLocationDAO(), XG7LobbyAPI.lobbyPlayerManager().getLobbyPlayerDAO());
    }

    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(new SetLobby(), new DeleteLobby(), new Lobbies(), new Lobby(), new ExecuteActionCommand(), new GamemodeCommand(), new OpenInventoryCommand(), new FlyCommand(), new BuildCommand(), new VanishCommand());
    }

    @Override
    public List<Listener> loadEvents() {
        return Arrays.asList(new LoginAndLogoutEvent(), new DefaultWorldEvents(), new DefaultPlayerEvents(), new LobbyCommandEvent(), new LaunchpadListener(), new MultiJumpingListener());
    }

    @Override
    public List<PacketListener> loadPacketEvents() {
        return null;
    }

    @Override
    public List<Task> loadRepeatingTasks() {

        List<Task> tasks = new ArrayList<>();

        if (Config.of("ads", this).get("enabled", Boolean.class).orElse(false)) {
            tasks.add(new AutoBroadcastTask());
        }

        return tasks;
    }

    @Override
    public void loadHelp() {

    }

    public void loadMenus() {
        ConfigManager configManager = XG7PluginsAPI.configManager(this);

        configManager.registerAdapter(new LobbyItemTypeAdapter());
        configManager.registerAdapter(new LobbyGUITypeAdapter());
        configManager.registerAdapter(new LobbyHotbarTypeAdapter());

        XG7LobbyAPI.customInventoryManager().loadInventories();

        XG7Menus menus = XG7Menus.getInstance();

        menus.registerMenus(new LobbiesMenu());
    }

    public void loadScores() {
        LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
        lobbyScoreManager.loadScores();

    }
}
