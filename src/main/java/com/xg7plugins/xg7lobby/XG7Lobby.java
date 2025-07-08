package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.PluginSetup;
import com.xg7plugins.cache.RedisCacheSection;
import com.xg7plugins.commands.core_commands.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigManager;
import com.xg7plugins.data.config.core.MainConfigSection;
import com.xg7plugins.data.database.dao.DAO;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.xg7lobby.commands.custom.CustomCommandManager;
import com.xg7plugins.xg7lobby.commands.lobby.DeleteLobby;
import com.xg7plugins.xg7lobby.commands.lobby.Lobbies;
import com.xg7plugins.xg7lobby.commands.lobby.Lobby;
import com.xg7plugins.xg7lobby.commands.lobby.SetLobby;
import com.xg7plugins.xg7lobby.commands.moderation.infraction.InfractionsMenuCommand;
import com.xg7plugins.xg7lobby.commands.moderation.KickCommand;
import com.xg7plugins.xg7lobby.commands.moderation.ban.BanCommand;
import com.xg7plugins.xg7lobby.commands.moderation.ban.BanIPCommand;
import com.xg7plugins.xg7lobby.commands.moderation.ban.UnbanCommand;
import com.xg7plugins.xg7lobby.commands.moderation.ban.UnbanIPCommand;
import com.xg7plugins.xg7lobby.commands.moderation.infraction.InfractionCommand;
import com.xg7plugins.xg7lobby.commands.moderation.mute.MuteCommand;
import com.xg7plugins.xg7lobby.commands.moderation.mute.UnMuteCommand;
import com.xg7plugins.xg7lobby.commands.toggle.*;
import com.xg7plugins.xg7lobby.commands.utils.ExecuteActionCommand;
import com.xg7plugins.xg7lobby.commands.utils.GamemodeCommand;
import com.xg7plugins.xg7lobby.commands.utils.OpenInventoryCommand;
import com.xg7plugins.xg7lobby.configs.*;
import com.xg7plugins.xg7lobby.events.air.LaunchpadListener;
import com.xg7plugins.xg7lobby.events.air.MultiJumpingListener;
import com.xg7plugins.xg7lobby.events.chat.AntiSpamListener;
import com.xg7plugins.xg7lobby.events.chat.AntiSwearingListener;
import com.xg7plugins.xg7lobby.events.chat.LockChatCommandListener;
import com.xg7plugins.xg7lobby.events.command.BlockCommandsListener;
import com.xg7plugins.xg7lobby.events.command.LobbyCommandListener;
import com.xg7plugins.xg7lobby.events.chat.MuteCommandListener;
import com.xg7plugins.xg7lobby.events.command.PVPBlockCommandsListener;
import com.xg7plugins.xg7lobby.events.command.PVPCommandListener;
import com.xg7plugins.xg7lobby.events.lobby.DefaultPlayerEvents;
import com.xg7plugins.xg7lobby.events.lobby.DefaultWorldEvents;
import com.xg7plugins.xg7lobby.events.lobby.LoginAndLogoutEvent;
import com.xg7plugins.xg7lobby.events.lobby.MOTDListener;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyGUITypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyHotbarTypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter.LobbyItemTypeAdapter;
import com.xg7plugins.xg7lobby.menus.default_menus.LobbiesMenu;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.location.LobbyManager;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu.InfractionsMenu;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import com.xg7plugins.xg7lobby.pvp.handlers.CombatLogHandler;
import com.xg7plugins.xg7lobby.scores.LobbyScoreManager;
import com.xg7plugins.xg7lobby.tasks.AntiSpamTask;
import com.xg7plugins.xg7lobby.tasks.AutoBroadcastTask;
import com.xg7plugins.xg7lobby.tasks.EffectsTask;
import com.xg7plugins.xg7lobby.tasks.WorldCyclesTask;
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
        configs = {"ads", "custom_commands", "events", "pvp", "scores"},
        configSections = {
                AdvertisementConfigs.class, ChatConfigs.class, ChatConfigs.AntiSwearing.class,
                ChatConfigs.AntiSpam.class, ChatConfigs.BlockCommands.class, EventConfigs.OnJoin.class,
                EventConfigs.OnFirstJoin.class, EventConfigs.OnQuit.class, LaunchpadConfigs.class,
                LobbyTeleportConfigs.class, MainConfigs.class, ModerationConfigs.class, MotdConfig.class,
                MultiJumpsConfigs.class, PlayerConfigs.class, PVPConfigs.class, PVPConfigs.OnJoinPvp.class,
                PVPConfigs.OnKill.class, PVPConfigs.OnLeavePvp.class, PVPConfigs.OnRespawn.class,
                WorldConfigs.class
        },
        reloadCauses = {"scores", "menus", "custom_commands"}

)
public final class XG7Lobby extends Plugin {

    @Override
    public void onLoad() {
        this.environmentConfig = new XG7LobbyEnvironment();
        super.onLoad();
    }

    @Override
    public void onEnable() {

        super.onEnable();

        debug.loading("Loading managers...");

        managerRegistry.registerManagers(
                new LobbyPlayerManager(),
                new LobbyManager(),
                new LobbyScoreManager(),
                new CustomCommandManager(),
                Config.of(this, PVPConfigs.class).isEnabled() ? new GlobalPVPManager() : null
        );

        if (Config.of(this, MainConfigs.class).isMenusEnabled()) {
            managerRegistry.registerManager(new CustomInventoryManager());
        }

        debug.loading("Loading scores...");

        loadScores();

        debug.loading("Loading menus...");

        loadMenus();

        debug.loading("Loading custom commands...");

        ManagerRegistry.get(this,  CustomCommandManager.class).registerCommands();

        debug.loading("XG7Lobby enabled.");
    }

    @Override
    public void onReload(ReloadCause cause) {

        super.onReload(cause);

        if (cause.equals("scores")) {
            debug.loading("Reloading scores...");
            LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
            lobbyScoreManager.reloadScores();
            debug.loading("Scores reloaded.");
        }
        if (cause.equals("menus")) {
            debug.loading("Reloading menus...");

            XG7Menus menus = XG7Menus.getInstance();
            menus.registerMenus(new LobbiesMenu(), new InfractionsMenu());

            CustomInventoryManager inventoryManager = XG7LobbyAPI.customInventoryManager();
            inventoryManager.reloadInventories();

            debug.loading("Menus reloaded.");
        }
        if (cause.equals("custom_commands")) {
            debug.loading("Reloading custom commands...");
            ManagerRegistry.get(this,  CustomCommandManager.class).reloadCommands();
            debug.loading("Custom commands reloaded.");
        }

    }

    @Override
    public void onDisable() {

        super.onDisable();

        LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
        lobbyScoreManager.unloadScores();
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
        return Arrays.asList(new SetLobby(), new DeleteLobby(), new Lobbies(), new Lobby(), new ExecuteActionCommand(), new GamemodeCommand(), new OpenInventoryCommand(), new FlyCommand(), new BuildCommand(), new VanishCommand(), new BanCommand(), new BanIPCommand(), new UnbanCommand(), new UnbanIPCommand(), new InfractionCommand(), new MuteCommand(), new UnMuteCommand(), new KickCommand(), new InfractionsMenuCommand(), new LockChatCommand(), new PVPCommand());
    }

    @Override
    public List<Listener> loadEvents() {

        List<Listener> listeners = new ArrayList<>(Arrays.asList(new PVPCommandListener(), new LoginAndLogoutEvent(), new DefaultWorldEvents(), new DefaultPlayerEvents(), new LobbyCommandListener(), new LaunchpadListener(), new MultiJumpingListener(), new MOTDListener(), new MuteCommandListener(), new AntiSpamListener(), new AntiSwearingListener(), new LockChatCommandListener(), new BlockCommandsListener(), new PVPBlockCommandsListener()));

        GlobalPVPManager pvpManager = XG7LobbyAPI.globalPVPManager();

        if (pvpManager != null) {
            listeners.addAll(pvpManager.getAllListenersHandlers());
        }

        return listeners;
    }

    @Override
    public List<PacketListener> loadPacketEvents() {
        return null;
    }

    @Override
    public List<TimerTask> loadRepeatingTasks() {

        List<TimerTask> tasks = new ArrayList<>();

        if (Config.of(this, AdvertisementConfigs.class).isEnabled()) tasks.add(new AutoBroadcastTask());
        tasks.add(new EffectsTask());
        tasks.add(new WorldCyclesTask());
        if (Config.of(this, ChatConfigs.AntiSpam.class).isEnabled() && Config.of(this, ChatConfigs.AntiSpam.class).getSpamTolerance() > 0) tasks.add(new AntiSpamTask());

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

        menus.registerMenus(new LobbiesMenu(), new InfractionsMenu());
    }

    public void loadScores() {
        LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
        lobbyScoreManager.loadScores();

    }
}
