package com.xg7plugins.xg7lobby.events.lobby;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.EventConfigs;
import com.xg7plugins.xg7lobby.configs.MainConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        EventConfigs.OnJoin joinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnJoin.class);
        EventConfigs.OnFirstJoin firstJoinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnFirstJoin.class);

        Player player = event.getPlayer();

        if (joinConfig.isSendJoinMessage()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                boolean inEnabledWorld = XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player);
                if (!inEnabledWorld && joinConfig.isSendJoinMessageOnlyOnLobby())
                    return;
                String langKey = firstJoinConfig.isEnabled() && !player.hasPlayedBefore() ? "messages.on-first-join"
                        : "messages.on-join";
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), langKey, Pair.of("target", player.getDisplayName()));
            });
        }

        boolean runAfterAuth = XG7PluginsAPI.isDependencyEnabled("nLogin") && joinConfig.isApplyConfigsAfterAuthenticate();

        if (joinConfig.isTpToLobby()) {
            String lobbyId = joinConfig.getLobbyToTpId();

            CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random")
                    ? XG7LobbyAPI.requestRandomLobbyLocation()
                    : XG7LobbyAPI.requestLobbyLocation(lobbyId);

            lobbyLocation.thenAccept(lobby -> {
                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {

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

        EventConfigs.OnQuit quitConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnQuit.class);

        if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
            player.getInventory().clear();
            XG7Menus.removePlayerMenuHolder(player.getUniqueId());
        }

        if (quitConfig.isSendQuitMessage()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                boolean inEnabledWorld = XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player);
                if (!inEnabledWorld && quitConfig.isSendQuitMessageOnlyOnLobby())
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

        EventConfigs.OnQuit quitConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnQuit.class);

        if (quitConfig.isRunEventsWhenChangeWorld())
            ActionsProcessor.process(quitConfig.getEvents(), player);

        if (XG7LobbyAPI.customInventoryManager() != null)
            XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

        PlayerConfigs configs = Config.of(XG7Lobby.getInstance(), PlayerConfigs.class);

        configs.reset(player);

    }

    protected static void handleWorldJoin(Player player, World newWorld, boolean alreadyTp) {
        EventConfigs.OnJoin joinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnJoin.class);
        EventConfigs.OnFirstJoin firstJoinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnFirstJoin.class);

        World previousBeforeActions = player.getWorld();
        boolean shouldRunEvents = player.getWorld() == newWorld || joinConfig.isRunEventsWhenReturnToTheWorld();

        if (shouldRunEvents) {
            ActionsProcessor.process(
                    firstJoinConfig.isEnabled() && !player.hasPlayedBefore() ? firstJoinConfig.getEvents()
                            : joinConfig.getEvents(),
                    player);
        }

        if (joinConfig.isTpToLobby() && !alreadyTp) {

            if (previousBeforeActions.getUID().equals(player.getWorld().getUID())) {

                String lobbyId = joinConfig.getLobbyToTpId();

                CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random")
                        ? XG7LobbyAPI.requestRandomLobbyLocation()
                        : XG7LobbyAPI.requestLobbyLocation(lobbyId);

                lobbyLocation.thenAccept(lobby -> {
                    XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
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
            XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
                if (joinConfig.isClearInventory())
                    player.getInventory().clear();

                if (joinConfig.isHeal())
                    player.setHealth(player.getMaxHealth());

                if (!XG7PluginsAPI.isEnabledWorld(XG7Lobby.getInstance(),
                        previousBeforeActions.equals(player.getWorld()) ? newWorld : player.getWorld()))
                    return;

                PlayerConfigs configs = Config.of(XG7Lobby.getInstance(), PlayerConfigs.class);
                configs.apply(player);

                XG7LobbyAPI.customInventoryManager()
                        .openMenu(Config.of(XG7Lobby.getInstance(), MainConfigs.class).getMainSelectorId(), player);

                lobbyPlayer.fly();
                lobbyPlayer.applyBuild();
                lobbyPlayer.applyHide();
                lobbyPlayer.applyInfractions();
            }), 50L);
        });
    }

}
