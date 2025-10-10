package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.boot.setup.Collaborator;
import com.xg7plugins.boot.setup.PluginSetup;
import com.xg7plugins.commands.impl.reload.ReloadCause;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.config.ConfigManager;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.data.database.dao.Repository;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.packetevents.PacketListener;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.help.chat.HelpChat;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Metrics;
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
import com.xg7plugins.xg7lobby.commands.utils.*;
import com.xg7plugins.xg7lobby.environment.XG7LobbyEnvironment;
import com.xg7plugins.xg7lobby.environment.XG7LobbyPlaceholderExpansion;
import com.xg7plugins.xg7lobby.events.air.LaunchpadListener;
import com.xg7plugins.xg7lobby.events.air.MultiJumpingListener;
import com.xg7plugins.xg7lobby.events.chat.AntiSpamListener;
import com.xg7plugins.xg7lobby.events.chat.AntiSwearingListener;
import com.xg7plugins.xg7lobby.events.chat.LockChatCommandListener;
import com.xg7plugins.xg7lobby.events.command.*;
import com.xg7plugins.xg7lobby.events.chat.MuteCommandListener;
import com.xg7plugins.xg7lobby.events.command.blocker.CommandAntiTabListener;
import com.xg7plugins.xg7lobby.events.command.blocker.CommandProcessListener;
import com.xg7plugins.xg7lobby.events.lobby.DefaultPlayerEvents;
import com.xg7plugins.xg7lobby.events.lobby.DefaultWorldEvents;
import com.xg7plugins.xg7lobby.events.lobby.LoginAndLogoutEvent;
import com.xg7plugins.xg7lobby.events.lobby.MOTDListener;
import com.xg7plugins.xg7lobby.events.lobby.NLoginListener;
import com.xg7plugins.xg7lobby.help.chat.AboutPage;
import com.xg7plugins.xg7lobby.help.chat.CustomCommandPage;
import com.xg7plugins.xg7lobby.help.chat.Index;
import com.xg7plugins.xg7lobby.help.chat.MenusGuidePage;
import com.xg7plugins.xg7lobby.help.form.CollaboratorsForm;
import com.xg7plugins.xg7lobby.help.form.XG7LobbyHelpForm;
import com.xg7plugins.xg7lobby.help.gui.ActionsMenu;
import com.xg7plugins.xg7lobby.help.gui.XG7LobbyHelpGUI;
import com.xg7plugins.xg7lobby.menus.custom.forms.CustomFormsManager;
import com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter.LobbyCustomFormAdapter;
import com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter.LobbyModalFormAdapter;
import com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter.LobbySimpleFormAdapter;
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
import com.xg7plugins.xg7lobby.scores.LobbyScoreManager;
import com.xg7plugins.xg7lobby.tasks.*;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
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
        configs = {"ads", "custom_commands", "events", "pvp"},
        reloadCauses = {"scores", "menus", "forms"},
        collaborators = {
                @Collaborator(uuid = "45766b7f-9789-40e1-bd0b-46fa0d032bde", name = "&aDaviXG7", role = "&bCreator of all plugin"),
                @Collaborator(uuid = "f12b8505-8b77-4046-9d86-8b5303690096", name = "&aSadnessSad", role = "&bBeta tester"),
                @Collaborator(uuid = "696581df-4256-4028-b55e-9452b4de40b6", name = "&aBultzzXG7", role = "&bBeta tester"),
                @Collaborator(uuid = "f66d01bf-0e1c-4800-9a50-060411bff0bd", name = "&aMintNonExistent (Gorrfy)", role = "&bBeta tester"),
                @Collaborator(uuid = "35e9eeda-84ce-497d-af08-7cf5d68a21c7", name = "&aDanielXG7", role = "&bBeta tester")
        }

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

        debug.loading("Loading metrics...");

        Metrics.getMetrics(this, 24625);

        debug.loading("Loading managers...");

        managerRegistry.registerManagers(
                new LobbyPlayerManager(),
                new LobbyManager(),
                new LobbyScoreManager(),
                new CustomCommandManager(),
                new GlobalPVPManager()
        );

        ConfigSection config = ConfigFile.mainConfigOf(this).root();

        if (config.get("custom-menus-enabled", false)) {
            managerRegistry.registerManager(new CustomInventoryManager());
        }
        if (config.get("custom-geyser-forms-enabled", false)) {
            managerRegistry.registerManager(new CustomFormsManager());
        }

        debug.loading("Loading scores...");

        loadScores();

        debug.loading("Loading menus...");

        loadMenus();

        debug.loading("Loading forms...");

        loadGeyserForms();

        debug.loading("Loading custom commands...");

        ManagerRegistry.get(this,  CustomCommandManager.class).registerCommands();
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
            CustomInventoryManager inventoryManager = XG7LobbyAPI.customInventoryManager();

            if (inventoryManager != null) {
                inventoryManager.reloadInventories();

                Bukkit.getOnlinePlayers().stream().filter(p -> XG7Plugins.getAPI().isInAnEnabledWorld(this, p))
                        .forEach(p -> {
                            XG7Plugins.getAPI().menus().closeAllMenus(p);
                            inventoryManager.openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-selector-id"), p);
                        });
            }

            debug.loading("Menus reloaded.");
        }
        if (cause.equals("forms")) {

            if (!XG7Plugins.getAPI().isGeyserFormsEnabled()) return;
            if (XG7LobbyAPI.customFormsManager() == null) return;

            debug.loading("Reloading forms...");

            CustomFormsManager formsManager = XG7LobbyAPI.customFormsManager();
            if (formsManager != null) formsManager.reloadForms();

            debug.loading("Forms reloaded.");
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
    public List<Repository<?,?>> loadRepositories() {
        return Arrays.asList(XG7LobbyAPI.lobbyManager().getLobbyLocationRepository(), XG7LobbyAPI.lobbyPlayerManager().getLobbyPlayerRepository());
    }

    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(new SetLobby(), new DeleteLobby(), new Lobbies(), new Lobby(), new ExecuteActionCommand(), new GamemodeCommand(), new OpenInventoryCommand(), new FlyCommand(), new BuildCommand(), new VanishCommand(), new BanCommand(), new BanIPCommand(), new UnbanCommand(), new UnbanIPCommand(), new InfractionCommand(), new MuteCommand(), new UnMuteCommand(), new KickCommand(), new InfractionsMenuCommand(), new LockChatCommand(), new PVPCommand(), new OpenFormCommand(), new ResetStatsCommand());
    }

    @Override
    public List<Listener> loadEvents() {

        List<Listener> listeners = new ArrayList<>(Arrays.asList(new PVPCommandListener(), new LoginAndLogoutEvent(), new DefaultWorldEvents(), new DefaultPlayerEvents(), new LobbyCommandListener(), new LaunchpadListener(), new MultiJumpingListener(), new MOTDListener(), new MuteCommandListener(), new AntiSpamListener(), new AntiSwearingListener(), new LockChatCommandListener(), new BlockCommandsListener(), new PVPBlockCommandsListener(), new CommandProcessListener(), new NLoginListener()));

        GlobalPVPManager pvpManager = XG7LobbyAPI.globalPVPManager();

        if (pvpManager.isEnabled()) {
            listeners.addAll(pvpManager.getAllListenersHandlers());
        }

        return listeners;
    }

    @Override
    public List<PacketListener> loadPacketEvents() {
        return Collections.singletonList(new CommandAntiTabListener());
    }

    @Override
    public List<TimerTask> loadRepeatingTasks() {

        List<TimerTask> tasks = new ArrayList<>();
        tasks.add(new ForeverActionsTask());
        tasks.add(new WorldCyclesTask());

        if (ConfigFile.of("ads", this).root().get("enabled")) tasks.add(new AutoBroadcastTask());

        ConfigSection config = ConfigFile.mainConfigOf(this).section("anti-spam");


        if (config.get("enabled", false) && config.get("spam-tolerance", 0) > 0) tasks.add(new AntiSpamTask());

        return tasks;
    }

    @Override
    public void loadHelp() {

        HelpGUI helpCommandGUI = new HelpGUI(this, new XG7LobbyHelpGUI());

        helpCommandGUI.registerMenu("actions", new ActionsMenu());

        HelpForm helpCommandForm = null;

        if (XG7Plugins.getAPI().isGeyserFormsEnabled()) {
            helpCommandForm = new HelpForm(new XG7LobbyHelpForm());

            helpCommandForm.registerForm("lobby-collaborators-help", new CollaboratorsForm());
        }

        HelpChat helpInChat = new HelpChat(this, new Index());
        helpInChat.registerPage(new MenusGuidePage());
        helpInChat.registerPage(new CustomCommandPage());
        helpInChat.registerPage(new AboutPage());

        this.helpMessenger = new HelpMessenger(this, helpCommandGUI, helpCommandForm, helpInChat);

    }

    @Override
    public Object loadPlaceholderExpansion() {
        return new XG7LobbyPlaceholderExpansion();
    }

    public void loadMenus() {

        XG7Menus menus = XG7Plugins.getAPI().menus();

        menus.registerMenus(new LobbiesMenu(), new InfractionsMenu());

        if (XG7LobbyAPI.customInventoryManager() == null) return;

        ConfigManager configManager = XG7Plugins.getAPI().configManager(this);

        configManager.registerAdapter(new LobbyItemTypeAdapter());
        configManager.registerAdapter(new LobbyGUITypeAdapter());
        configManager.registerAdapter(new LobbyHotbarTypeAdapter());

        XG7LobbyAPI.customInventoryManager().loadInventories();

    }

    public void loadGeyserForms() {

        if (!XG7Plugins.getAPI().isGeyserFormsEnabled()) return;
        if (XG7LobbyAPI.customFormsManager() == null) return;

        ConfigManager configManager = XG7Plugins.getAPI().configManager(this);

        configManager.registerAdapter(new LobbyCustomFormAdapter());
        configManager.registerAdapter(new LobbyModalFormAdapter());
        configManager.registerAdapter(new LobbySimpleFormAdapter());

        XG7LobbyAPI.customFormsManager().loadForms();

    }

    public void loadScores() {
        LobbyScoreManager lobbyScoreManager = ManagerRegistry.get(this,  LobbyScoreManager.class);
        lobbyScoreManager.loadScores();

    }
}
