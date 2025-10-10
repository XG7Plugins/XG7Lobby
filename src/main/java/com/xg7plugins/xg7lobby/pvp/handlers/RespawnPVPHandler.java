package com.xg7plugins.xg7lobby.pvp.handlers;


import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.XG7Menus;
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

        
        
        

        XG7LobbyAPI.requestLobbyLocation(pvpConfigs.get("pvp-lobby")).thenAccept(l -> XG7Plugins.getAPI().taskManager().scheduleSync(BukkitTask.of( () -> {
            
            
            if (l == null) {
                
                return;
            }

            

            try {
                l.teleport(player);
                
            } catch (Exception e) {
                
                e.printStackTrace();
            }

        }), 2000L));

        if (XG7LobbyAPI.customInventoryManager() != null) {
            XG7Plugins.getAPI().menus().closeAllMenus(player);
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
