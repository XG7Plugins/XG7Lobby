package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.pvp.event.PlayerJoinPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinPVPHandler implements PVPHandler, LobbyListener {

    private final PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

    @Override
    public void handle(Player player, Object... args) {

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-join");

        PVPConfigs.OnJoinPvp config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnJoinPvp.class);

        player.setGameMode(GameMode.SURVIVAL);
        Config.of(XG7Lobby.getInstance(), PlayerConfigs.class).reset(player);

        if (XG7LobbyAPI.customInventoryManager() != null) {
            XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

            XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-pvp-selector-id", String.class).orElse("pvp"), player);
        }

        if (config.isHeal()) player.setHealth(player.getMaxHealth());

        if (config.isTeleportToPvpLobby()) {
            XG7LobbyAPI.requestLobbyLocation(pvpConfigs.getPvpLobby()).thenAccept(l -> l.teleport(player));
        }

        hidePlayers(player);
        ActionsProcessor.process(config.getActions(), player);

        Bukkit.getPluginManager().callEvent(new PlayerJoinPVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.isEnabled();
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Lobby.getInstance(), () -> hidePlayers(player)), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Lobby.getInstance(), () -> hidePlayers(event.getPlayer())), 1L);
    }

    private void hidePlayers(Player player) {
        if (!pvpConfigs.isHidePlayersNotInPvp()) return;

        boolean isInPvP = XG7LobbyAPI.isPlayerInPVP(player);

        for (Player other : Bukkit.getOnlinePlayers()) {

            if (player.equals(other)) continue;

            boolean otherInPvP = XG7LobbyAPI.isPlayerInPVP(other);

            if (isInPvP && !otherInPvP) {
                player.hidePlayer(other);
            }

            if (!isInPvP && otherInPvP) {
                other.hidePlayer(player);
            }

            if (isInPvP && otherInPvP) {
                player.showPlayer(other);
                other.showPlayer(player);
            }
        }
    }
}
