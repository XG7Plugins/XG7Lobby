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
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.EventConfigs;
import com.xg7plugins.xg7lobby.configs.MainConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.configs.XG7LobbyEnvironment;
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
            if (!saved) event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error saving player data!");
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
                if (!XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player) && joinConfig.isSendJoinMessageOnlyOnLobby()) return;
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), firstJoinConfig.isEnabled() && !player.hasPlayedBefore() ? "messages.on-first-join" : "messages.on-join", Pair.of("target", player.getDisplayName()));
            });
        }

        if (!XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

        onWorldJoin(player, player.getWorld());
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
                if (!XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player) && quitConfig.isSendQuitMessageOnlyOnLobby())
                    return;
                Text.sendTextFromLang(p, XG7Lobby.getInstance(), "messages.on-quit", Pair.of("target", player.getName())).join();
            });

        }

        onWorldLeave(player, null);
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        EventConfigs.OnJoin joinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnJoin.class);
        EventConfigs.OnFirstJoin firstJoinConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnFirstJoin.class);

        if (player.getWorld() == newWorld || joinConfig.isRunEventsWhenReturnToTheWorld() || newWorld == null) {
            ActionsProcessor.process(firstJoinConfig.isEnabled() && !player.hasPlayedBefore() ? firstJoinConfig.getEvents() : joinConfig.getEvents(), player);
        }

        if (joinConfig.isTpToLobby()) {

            String lobbyId = joinConfig.getLobbyToTpId();

            CompletableFuture<LobbyLocation> lobbyLocation = lobbyId.equalsIgnoreCase("random") ? XG7LobbyAPI.requestRandomLobbyLocation() : XG7LobbyAPI.requestLobbyLocation(lobbyId);

            lobbyLocation.thenAccept(lobby -> {
                if (lobby.getLocation() == null) {
                    Text.sendTextFromLang(player,XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist" + (player.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                    return;
                }
                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> lobby.teleport(player)));
            });

        }

        if (joinConfig.isClearInventory()) player.getInventory().clear();

        XG7LobbyAPI.customInventoryManager().openMenu(Config.of(XG7Lobby.getInstance(), MainConfigs.class).getMainSelectorId(), player);

        PlayerConfigs configs = Config.of(XG7Lobby.getInstance(), PlayerConfigs.class);

        configs.apply(player);

        if (joinConfig.isHeal()) player.setHealth(player.getMaxHealth());

        XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            lobbyPlayer.fly();
            lobbyPlayer.applyBuild();
            lobbyPlayer.applyHide();
            lobbyPlayer.applyInfractions();
        });
    }

    @Override
    public void onWorldLeave(Player player, World newWorld) {

        EventConfigs.OnQuit quitConfig = Config.of(XG7Lobby.getInstance(), EventConfigs.OnQuit.class);


        XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

        if (player.getWorld().getUID().equals(newWorld.getUID()) || quitConfig.isRunEventsWhenReturnToTheWorld()) {
            ActionsProcessor.process(quitConfig.getEvents(), player);
        }

        PlayerConfigs configs = Config.of(XG7Lobby.getInstance(), PlayerConfigs.class);

        configs.reset(player);
    }
}
