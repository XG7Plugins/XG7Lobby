package com.xg7plugins.xg7lobby.events.lobby_events;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.XG7LobbyConfig;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.lobby.location.LobbyLocation;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerManager;
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
        lobbyPlayerManager.savePlayer(new LobbyPlayer(event.getUniqueId()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Config config = Config.of("events", XG7Lobby.getInstance());

        Player player = event.getPlayer();

        boolean messageOnlyInLobby = config.get("on-join.send-join-message-only-on-lobby", Boolean.class).orElse(false);

        boolean firstJoinEnabled = config.get("on-first-join.enabled", Boolean.class).orElse(false);
        boolean sendJoinMessage = config.get("on-join.send-join-message", Boolean.class).orElse(true);

        if (sendJoinMessage) {

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), player) && messageOnlyInLobby) return;
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), firstJoinEnabled && !player.hasPlayedBefore() ? "messages.on-first-join" : "messages.on-join").join();
            });
        }

        if (!XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), player)) return;

        onWorldJoin(player, player.getWorld());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();

        if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
            player.getInventory().clear();
            XG7Menus.removePlayerMenuHolder(player.getUniqueId());
        }

        boolean messageOnlyInLobby = Config.of("events", XG7Lobby.getInstance()).get("on-join.send-join-message-only-on-lobby", Boolean.class).orElse(false);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!XG7PluginsAPI.isInWorldEnabled(XG7Lobby.getInstance(), player) && messageOnlyInLobby) return;
            Text.sendTextFromLang(p, XG7Lobby.getInstance(), "messages.on-quit", Pair.of("target", player.getName())).join();
        });

        onWorldLeave(player, player.getWorld());
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        Config config = Config.of("events", XG7Lobby.getInstance());

        LobbyPlayerManager lobbyPlayerManager = XG7LobbyAPI.lobbyPlayerManager();

        lobbyPlayerManager.getPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            lobbyPlayer.fly();
            lobbyPlayer.setHidingPlayers(lobbyPlayer.isHidingPlayers());
        });

        if (player.getWorld() == newWorld || config.get("on-join.run-events-when-return-to-the-world", Boolean.class).orElse(false)) {
            ActionsProcessor.process(config.getList(config.get("on-first-join.enabled", Boolean.class).orElse(false) && !player.hasPlayedBefore() ? "on-first-join.events" : "on-join.events", String.class).orElse(Collections.emptyList()), player);
        }

        if (config.get("on-join.tp-to-lobby", Boolean.class).orElse(true)) {

            String lobbyId = config.get("lobby-to-tp-id", String.class).orElse("random");

            CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random") ? XG7LobbyAPI.requestRandomLobbyLocation() : XG7LobbyAPI.requestLobbyLocation(lobbyId);

            lobbyLocation.thenAccept(lobby -> {
                if (lobby.getLocation() == null) {
                    Text.sendTextFromLang(player,XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist" + (player.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                    return;
                }
                XG7PluginsAPI.taskManager().runSyncTask(XG7Lobby.getInstance(), () -> lobby.teleport(player));
            });

        }

        Config mainConfig = Config.mainConfigOf(XG7Lobby.getInstance());

        XG7LobbyConfig lobbyConfig = XG7Lobby.getInstance().getEnvironmentConfig();
        lobbyConfig.getPlayerConfigs().apply(player);

        if (config.get("on-join.heal", Boolean.class).orElse(true)) player.setHealth(player.getMaxHealth());
        if (config.get("on-join.clear-inventory", Boolean.class).orElse(true)) player.getInventory().clear();
    }

    @Override
    public void onWorldLeave(Player player, World newWorld) {
        Config config = Config.of("events", XG7Lobby.getInstance());

        player.closeInventory();

        if (player.getWorld() == newWorld || config.get("on-quit.run-events-when-return-to-the-world", Boolean.class).orElse(false)) {
            ActionsProcessor.process(config.getList("on-quit.events", String.class).orElse(Collections.emptyList()), player);
        }

        XG7LobbyConfig lobbyConfig = XG7Lobby.getInstance().getEnvironmentConfig();
        lobbyConfig.getPlayerConfigs().reset(player);
    }
}
