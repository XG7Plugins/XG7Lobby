package com.xg7plugins.xg7lobby;

import com.xg7plugins.XG7Plugins;
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
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.help.HelpMessenger;
import com.xg7plugins.help.chat.HelpChat;
import com.xg7plugins.help.form.HelpForm;
import com.xg7plugins.help.menu.HelpGUI;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.PluginKey;
import com.xg7plugins.xg7lobby.commands.custom.CustomCommandManager;
import com.xg7plugins.xg7lobby.commands.entities.HologramsCommand;
import com.xg7plugins.xg7lobby.commands.entities.NPCsCommand;
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
import com.xg7plugins.xg7lobby.events.chat.*;
import com.xg7plugins.xg7lobby.holograms.HologramsManager;
import com.xg7plugins.xg7lobby.menus.default_menus.SettingsMenu;
import com.xg7plugins.xg7lobby.npcs.NPCsManager;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyPlaceholderExpansion;
import com.xg7plugins.xg7lobby.events.air.LaunchpadListener;
import com.xg7plugins.xg7lobby.events.air.MultiJumpingListener;
import com.xg7plugins.xg7lobby.events.command.*;
import com.xg7plugins.xg7lobby.events.command.blocker.CommandAntiTabListener;
import com.xg7plugins.xg7lobby.events.command.blocker.CommandProcessListener;
import com.xg7plugins.xg7lobby.events.lobby.DefaultPlayerEvents;
import com.xg7plugins.xg7lobby.events.lobby.DefaultWorldEvents;
import com.xg7plugins.xg7lobby.events.lobby.LoginAndLogoutEvent;
import com.xg7plugins.xg7lobby.events.lobby.MOTDListener;
import com.xg7plugins.xg7lobby.events.lobby.NLoginListener;
import com.xg7plugins.xg7lobby.help.chat.AboutPage;
import com.xg7plugins.xg7lobby.help.chat.Index;
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
import com.xg7plugins.xg7lobby.data.location.LobbyLocationManager;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu.InfractionsMenu;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import com.xg7plugins.xg7lobby.events.lobby.QueueListener;
import com.xg7plugins.xg7lobby.queue.QueueManager;
import com.xg7plugins.xg7lobby.tasks.QueueTask;
import com.xg7plugins.xg7lobby.scores.LobbyScoreManager;
import com.xg7plugins.xg7lobby.tasks.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
        configs = {"ads", "custom_commands", "events", "pvp", "data/data"},
        reloadCauses = {"scores", "menus", "forms", "holograms", "npcs", "queues"},
        collaborators = {
                @Collaborator(uuid = "45766b7f-9789-40e1-bd0b-46fa0d032bde", name = "&aDaviXG7", role = "&bCreator of all plugin"),
                @Collaborator(uuid = "f12b8505-8b77-4046-9d86-8b5303690096", name = "&aSadnessSad", role = "&bBeta tester"),
                @Collaborator(uuid = "696581df-4256-4028-b55e-9452b4de40b6", name = "&aBultzzXG7", role = "&bBeta tester"),
                @Collaborator(uuid = "f66d01bf-0e1c-4800-9a50-060411bff0bd", name = "&aMintNonExistent (Gorrfy)", role = "&bBeta tester"),
                @Collaborator(uuid = "35e9eeda-84ce-497d-af08-7cf5d68a21c7", name = "&aDanielXG7", role = "&bBeta tester")
        },
        metricsId = 24625
)
public final class XG7Lobby extends Plugin {

    private LobbyPlayerManager lobbyPlayerManager;
    private LobbyLocationManager lobbyManager;
    private LobbyScoreManager lobbyScoreManager;
    private CustomCommandManager customCommandManager;
    private GlobalPVPManager globalPVPManager;
    private CustomInventoryManager customInventoryManager;
    private CustomFormsManager customFormsManager;
    private HologramsManager hologramsManager;
    private NPCsManager npcsManager;
    private QueueManager queueManager;

    /**
     * Constructor for the Plugin class.
     *
     * @param plugin The JavaPlugin instance representing the plugin
     */
    public XG7Lobby(JavaPlugin plugin) {
        super(plugin);
        this.api = new XG7LobbyAPI(this);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {

        debug.info("load", "Loading managers...");

        this.lobbyPlayerManager = new LobbyPlayerManager();
        this.lobbyManager = new LobbyLocationManager();
        this.lobbyScoreManager = new LobbyScoreManager();
        this.customCommandManager = new CustomCommandManager();
        this.globalPVPManager = new GlobalPVPManager();

        ConfigSection config = ConfigFile.mainConfigOf(this).root();

        if (config.get("custom-menus-enabled", false)) {
            this.customInventoryManager = new CustomInventoryManager();
        }
        if (config.get("custom-geyser-forms-enabled", false)) {
            this.customFormsManager = new CustomFormsManager();
        }
        if (config.get("holograms-enabled", false)) {
            this.hologramsManager = new HologramsManager(this);
        }
        if (config.get("npcs-enabled", false)) {
            this.npcsManager = new NPCsManager(this);
        }
        if (config.getFile().section("queue-system").get("enabled", false)) {
            this.queueManager = new QueueManager();
            queueManager.init();
        }

        debug.info("load", "Loading scores...");
        lobbyScoreManager.loadScores();

        debug.info("load", "Loading custom commands...");
        this.customCommandManager = new CustomCommandManager();
        this.customCommandManager.registerCommands();
    }

    @Override
    public void onReload(ReloadCause cause) {

        super.onReload(cause);

        if (cause.equals("scores")) {
            debug.info("load", "Reloading scores...");
            lobbyScoreManager.reloadScores();
            debug.info("load", "Scores reloaded.");
        }
        if (cause.equals("menus")) {
            debug.info("load", "Reloading menus...");

            if (customInventoryManager != null) {
                customInventoryManager.reloadInventories();

                Bukkit.getOnlinePlayers().stream().filter(p -> XG7Plugins.getAPI().isInAnEnabledWorld(this, p))
                        .forEach(p -> {
                            XG7Plugins.getAPI().menus().closeAllMenus(p);
                            customInventoryManager.openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-selector-id"), p);
                        });
            }

            debug.info("load", "Menus reloaded.");
        }

        if (cause.equals("holograms")) {
            if (hologramsManager != null) {
                debug.info("load", "Reloading holograms...");

                hologramsManager.unregisterAllHolograms();
                hologramsManager.loadHolograms();

                debug.info("load", "Holograms reloaded.");
            }
        }

        if (cause.equals("npcs")) {
            if (npcsManager != null) {
                debug.info("load", "Reloading npcs...");

                npcsManager.unregisterAllNPCs();
                npcsManager.loadNPCs();

                debug.info("load", "NPCs reloaded.");
            }
        }

        if (cause.equals("forms")) {

            if (!XG7Plugins.getAPI().isGeyserFormsEnabled()) return;
            if (customFormsManager == null) return;

            debug.info("load", "Reloading forms...");

            if (customFormsManager != null) customFormsManager.reloadForms();

            debug.info("load", "Forms reloaded.");
        }

        if (cause.equals("queues")) {

            if (queueManager != null) {
                queueManager.clear();
                queueManager.init();
            }
        }

    }

    @Override
    public void onDisable() {
        lobbyScoreManager.unloadScores();
    }

    public static XG7Lobby getInstance() {
        return XG7LobbyLoader.getPluginInstance();
    }

    @Override
    public Class<? extends Entity<?,?>>[] loadDBEntities() {
        return new Class[]{LobbyPlayer.class, LobbyLocation.class};
    }

    @Override
    public List<Repository<?,?>> loadRepositories() {
        return Arrays.asList(XG7Lobby.getAPI().lobbyManager().getLobbyLocationRepository(), XG7Lobby.getAPI().lobbyPlayerManager().getLobbyPlayerRepository());
    }

    @Override
    public List<Command> loadCommands() {
        return Arrays.asList(
                new SetLobby(), new DeleteLobby(), new Lobbies(),
                new Lobby(), new ExecuteActionCommand(), new GamemodeCommand(),
                new OpenInventoryCommand(), new FlyCommand(), new BuildCommand(),
                new VanishCommand(), new BanCommand(), new BanIPCommand(),
                new UnbanCommand(), new UnbanIPCommand(), new InfractionCommand(),
                new MuteCommand(), new UnMuteCommand(), new KickCommand(),
                new InfractionsMenuCommand(), new LockChatCommand(), new PVPCommand(),
                new OpenFormCommand(), new ResetStatsCommand(), new HologramsCommand(hologramsManager),
                new NPCsCommand(npcsManager), new QueueCommand(queueManager), new ChatToggleCommand(),
                new SettingsCommand()
        );
    }

    @Override
    public List<Listener> loadEvents() {

        List<Listener> listeners = new ArrayList<>(Arrays.asList(new PVPCommandListener(),
                new LoginAndLogoutEvent(), new DefaultWorldEvents(), new DefaultPlayerEvents(),
                new LobbyCommandListener(), new LaunchpadListener(), new MultiJumpingListener(),
                new MOTDListener(), new MuteCommandListener(), new AntiSpamListener(),
                new AntiSwearingListener(), new LockChatCommandListener(), new BlockCommandsListener(),
                new PVPBlockCommandsListener(), new CommandProcessListener(), new NLoginListener(),
                new QueueListener(queueManager)
        ));

        GlobalPVPManager pvpManager = XG7Lobby.getAPI().globalPVPManager();

        if (pvpManager.isEnabled()) {
            listeners.addAll(pvpManager.getAllListenersHandlers());
        }

        return listeners;
    }

    @Override
    public List<PacketListener> loadPacketEvents() {
        return Arrays.asList(
                new CommandAntiTabListener(), new HideChatListener()
        );
    }

    @Override
    public List<TimerTask> loadRepeatingTasks() {

        List<TimerTask> tasks = new ArrayList<>();
        tasks.add(new ForeverActionsTask());
        tasks.add(new WorldCyclesTask());

        if (ConfigFile.of("ads", this).root().get("enabled")) tasks.add(new AutoBroadcastTask());

        ConfigSection config = ConfigFile.mainConfigOf(this).root();

        if (config.child("anti-spam").get("enabled", false)
                && config.child("anti-spam").get("spam-tolerance", 0) > 0)
            tasks.add(new AntiSpamTask());

        if (config.child("queue-system").get("enabled", false)) tasks.add(new QueueTask(queueManager));

        return tasks;
    }

    @Override
    public HelpMessenger loadHelp() {
        HelpGUI helpCommandGUI = new HelpGUI(this, new XG7LobbyHelpGUI());

        helpCommandGUI.registerMenu("actions", new ActionsMenu());

        HelpForm helpCommandForm = null;

        if (XG7Plugins.getAPI().isGeyserFormsEnabled()) {
            helpCommandForm = new HelpForm(new XG7LobbyHelpForm());

            helpCommandForm.registerForm("lobby-collaborators-help", new CollaboratorsForm());
        }

        HelpChat helpInChat = new HelpChat(this, new Index());
        helpInChat.registerPage(new AboutPage());

        return new HelpMessenger(this, helpCommandGUI, helpCommandForm, helpInChat);
    }

    @Override
    public Object loadPlaceholderExpansion() {
        return new XG7LobbyPlaceholderExpansion();
    }

    @Override
    public List<BasicMenu> loadMenus() {

        List<BasicMenu> menus = new ArrayList<>();
        menus.add(new LobbiesMenu());
        menus.add(new InfractionsMenu());
        menus.add(new SettingsMenu());

        if (XG7Lobby.getAPI().customInventoryManager() == null) {
            return menus;
        }

        ConfigManager configManager = XG7Plugins.getAPI().configManager(this);

        configManager.registerAdapter(new LobbyItemTypeAdapter());
        configManager.registerAdapter(new LobbyGUITypeAdapter());
        configManager.registerAdapter(new LobbyHotbarTypeAdapter());

        XG7Lobby.getAPI().customInventoryManager().loadInventories();

        return menus;
    }

    @Override
    public List<Form<?,?>> loadForms() {

        if (XG7Lobby.getAPI().customFormsManager() == null) return Collections.emptyList();

        ConfigManager configManager = XG7Plugins.getAPI().configManager(this);

        configManager.registerAdapter(new LobbyCustomFormAdapter());
        configManager.registerAdapter(new LobbyModalFormAdapter());
        configManager.registerAdapter(new LobbySimpleFormAdapter());

        try {
            XG7Lobby.getAPI().customFormsManager().loadForms();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyList();
    }

    @Override
    public List<Hologram> loadHolograms() {

        if (XG7Lobby.getAPI().hologramsManager() != null) {
            XG7Lobby.getAPI().hologramsManager().loadHolograms();
        }

        return Collections.emptyList();
    }

    @Override
    public List<NPC> loadNPCs() {

        if (XG7Lobby.getAPI().npcsManager() != null) {
            XG7Lobby.getAPI().npcsManager().loadNPCs();
        }

        return Collections.emptyList();
    }

    public static XG7LobbyAPI getAPI() {
        return (XG7LobbyAPI) getInstance().getApi();
    }

    public static PluginKey keyOf(String id) {
        return new PluginKey(XG7Lobby.getInstance(), id);
    }
}
