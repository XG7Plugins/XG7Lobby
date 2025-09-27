package com.xg7plugins.xg7lobby.pvp.handlers;


import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.pvp.event.PlayerRespawnInPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;

public class RespawnPVPHandler implements PVPHandler, Listener {

    private final ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).section("on-respawn");
    private final ConfigSection pvpConfigs = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

    @Override
    public void handle(Player player, Object... args) {

        System.out.println("LOBBY: " + pvpConfigs.get("pvp-lobby"));
        System.out.println("ACTIONS: " + config.getList("actions", String.class).orElse(Collections.emptyList()));
        System.out.println("TP TO LOBBY: " + config.get("teleport-to-pvp-lobby", false));

        XG7LobbyAPI.requestLobbyLocation(pvpConfigs.get("pvp-lobby")).thenAccept(l -> XG7PluginsAPI.taskManager().scheduleSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
            System.out.println("TELEPORTING TO PVP LOBBY");
            System.out.println("LOBBY: " + l);
            if (l == null) {
                System.out.println("LOCATION IS NULL!");
                return;
            }

            System.out.println("PLAYER BEFORE TP: " + player.getName() + " at " + player.getLocation());

            try {
                l.teleport(player);
                System.out.println("PLAYER AFTER TP: " + player.getName() + " at " + player.getLocation());
            } catch (Exception e) {
                System.out.println("ERROR DURING TELEPORT:");
                e.printStackTrace();
            }

        }), 2000L));

        if (XG7LobbyAPI.customInventoryManager() != null) {
            XG7LobbyAPI.customInventoryManager().closeAllMenus(player);
            XG7LobbyAPI.customInventoryManager().openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-pvp-selector-id", "pvp"), player);
        }

        ActionsProcessor.process(config.getList("actions", String.class).orElse(Collections.emptyList()), player);

        Bukkit.getPluginManager().callEvent(new PlayerRespawnInPVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.get("enabled", false);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if (!XG7LobbyAPI.globalPVPManager().isInPVP(player)) return;
        handle(player);
    }
}
