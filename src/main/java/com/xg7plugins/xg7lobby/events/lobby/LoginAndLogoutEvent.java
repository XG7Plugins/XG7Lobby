package com.xg7plugins.xg7lobby.events.lobby;

import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.events.LobbyListener;

import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.environment.LobbyApplier;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class LoginAndLogoutEvent implements LobbyListener {

    private final LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @SneakyThrows
    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        try {
            boolean saved = lobbyPlayerManager.savePlayer(new LobbyPlayer(event.getUniqueId()));
            if (!saved)
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error saving player data!");
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error saving player data!");
            throw new RuntimeException(e);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        ConfigFile config = ConfigFile.of("events", XG7Lobby.getInstance());

        ConfigSection joinConfig = config.section("on-join");
        ConfigSection firstJoinConfig = config.section("on-first-join");

        Player player = event.getPlayer();

        if (joinConfig.get("send-join-message")) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                boolean inEnabledWorld = XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player);
                if (!inEnabledWorld && joinConfig.get("send-join-message-only-in-lobby", false))
                    return;
                String langKey = firstJoinConfig.get("enabled", false) && !player.hasPlayedBefore() ? "messages.on-first-join"
                        : "messages.on-join";
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), langKey, Pair.of("target", player.getDisplayName()));
            });
        }

        boolean runAfterAuth = XG7PluginsAPI.isDependencyEnabled("nLogin") && joinConfig.get("apply-configs-after-authenticate", false);

        if (joinConfig.get("tp-to-lobby")) {
            String lobbyId = joinConfig.get("tp-to-lobby-id", "random");

            CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random")
                    ? XG7LobbyAPI.requestRandomLobbyLocation()
                    : XG7LobbyAPI.requestLobbyLocation(lobbyId);

            lobbyLocation.thenAccept(lobby -> {
                XG7PluginsAPI.taskManager().runSync(BukkitTask.of( () -> {

                    World previousWorld = player.getWorld();

                    if (lobby == null || lobby.getLocation() == null) {
                        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist"
                                + (player.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                        if (XG7PluginsAPI.isEnabledWorld(XG7Lobby.getInstance(), previousWorld) && !(runAfterAuth))
                            handleWorldJoin(player, player.getWorld(), true);
                        return;
                    }

                    lobby.teleport(player);
                    if (previousWorld.getUID().equals(player.getWorld().getUID()) && !(runAfterAuth)) {
                        handleWorldJoin(player, player.getWorld(), true);
                        return;
                    }
                }));
            });

            return;
        }

        if (runAfterAuth) return;

        handleWorldJoin(player, player.getWorld(), false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();

        ConfigSection quitConfig = ConfigFile.of("events", XG7Lobby.getInstance()).section("on-quit");

        if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
            player.getInventory().clear();
            XG7Menus.removePlayerMenuHolder(player.getUniqueId());
        }

        if (quitConfig.get("send-quit-message")) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                boolean inEnabledWorld = XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player);
                if (!inEnabledWorld && quitConfig.get("send-quit-message-only-in-lobby", false))
                    return;
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), "messages.on-quit",
                        Pair.of("target", player.getName())).join();
            });
        }

        onWorldLeave(player, null);
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        handleWorldJoin(player, newWorld, false);
    }

    @Override
    public void onWorldLeave(Player player, World newWorld) {

        ConfigSection quitConfig = ConfigFile.of("events", XG7Lobby.getInstance()).section("on-quit");

        if (quitConfig.get("run-actions-when-change-world", true))
            ActionsProcessor.process(quitConfig.getList("actions", String.class).orElse(Collections.emptyList()), player);

        if (XG7LobbyAPI.customInventoryManager() != null)
            XG7Menus.getInstance().closeAllMenus(player);

        LobbyApplier.reset(player);

    }

    protected static void handleWorldJoin(Player player, World newWorld, boolean alreadyTp) {
        ConfigFile config = ConfigFile.of("events", XG7Lobby.getInstance());

        ConfigSection joinConfig = config.section("on-join");
        ConfigSection firstJoinConfig = config.section("on-first-join");

        World previousBeforeActions = player.getWorld();
        boolean shouldRunEvents = player.getWorld() == newWorld || joinConfig.get("run-actions-when-return-to-the-lobby", true);

        if (shouldRunEvents) {
            XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of( () -> ActionsProcessor.process(
                    firstJoinConfig.get("enabled", true) && !player.hasPlayedBefore() ? firstJoinConfig.getList("actions", String.class).orElse(Collections.emptyList())
                            : joinConfig.getList("actions", String.class).orElse(Collections.emptyList()),
                    player)), 100L);
        }

        if (joinConfig.get("tp-to-lobby", true) && !alreadyTp) {

            if (previousBeforeActions.getUID().equals(player.getWorld().getUID())) {

                String lobbyId = joinConfig.get("tp-to-lobby-id", "random");

                CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random")
                        ? XG7LobbyAPI.requestRandomLobbyLocation()
                        : XG7LobbyAPI.requestLobbyLocation(lobbyId);

                lobbyLocation.thenAccept(lobby -> {
                    XG7PluginsAPI.taskManager().runSync(BukkitTask.of( () -> {
                        if (lobby == null || lobby.getLocation() == null) {
                            Text.sendTextFromLang(player, XG7Lobby.getInstance(),
                                    "lobby.on-teleport.on-error-doesnt-exist"
                                            + (player.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                            return;
                        }
                        lobby.teleport(player);
                    }));
                });
            }

        }

        XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of( () -> {
                if (joinConfig.get("clear-inventory", false))
                    player.getInventory().clear();

                if (joinConfig.get("heal", true))
                    player.setHealth(player.getMaxHealth());

                if (!XG7PluginsAPI.isEnabledWorld(XG7Lobby.getInstance(),
                        previousBeforeActions.equals(player.getWorld()) ? newWorld : player.getWorld()))
                    return;

                LobbyApplier.apply(player);

                XG7LobbyAPI.customInventoryManager()
                        .openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-selector-id"), player);

                lobbyPlayer.fly();
                lobbyPlayer.applyBuild();
                lobbyPlayer.applyHide();
                lobbyPlayer.applyInfractions();
            }), 50L);
        });
    }

}
