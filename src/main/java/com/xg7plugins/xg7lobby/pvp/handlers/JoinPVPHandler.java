package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;

import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.pvp.event.PlayerJoinPVPEvent;
import com.xg7plugins.xg7lobby.events.LobbyApplier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;

public class JoinPVPHandler implements PVPHandler, LobbyListener {

    private final ConfigSection pvpConfigs = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();
    private final ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).section("on-join-pvp");

    @Override
    public void handle(Player player, Object... args) {

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-join");

        player.setGameMode(GameMode.SURVIVAL);
        LobbyApplier.reset(player);

        if (XG7Lobby.getAPI().customInventoryManager() != null) {
            XG7Plugins.getAPI().menus().closeAllMenus(player);

            XG7Lobby.getAPI().customInventoryManager().openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-pvp-selector-id","pvp"), player);
        }

        if (config.get("heal")) player.setHealth(player.getMaxHealth());

        if (config.get("teleport-to-pvp-lobby", false)) {
            XG7Lobby.getAPI().requestLobbyLocation(pvpConfigs.get("pvp-lobby")).thenAccept(l -> l.teleport(player));
        }

        hidePlayers(player);
        ActionsProcessor.process(config.getList("actions", String.class).orElse(Collections.emptyList()), player);

        Bukkit.getPluginManager().callEvent(new PlayerJoinPVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.get("enabled", false);
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> hidePlayers(player)), 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> hidePlayers(event.getPlayer())), 1L);
    }

    private void hidePlayers(Player player) {
        if (!pvpConfigs.get("hide-players-not-in-pvp", false)) return;

        boolean isInPvP = XG7Lobby.getAPI().isPlayerInPVP(player);

        for (Player other : Bukkit.getOnlinePlayers()) {

            if (player.equals(other)) continue;

            boolean otherInPvP = XG7Lobby.getAPI().isPlayerInPVP(other);

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
