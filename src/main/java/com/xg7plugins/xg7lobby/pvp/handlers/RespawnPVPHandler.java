package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.event.PlayerRespawnInPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnPVPHandler implements PVPHandler, Listener {

    private final PVPConfigs.OnRespawn config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnRespawn.class);
    private final PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

    @Override
    public void handle(Player player, Object... args) {

        if (config.isTeleportToPvpLobby()) {
            XG7LobbyAPI.requestLobbyLocation(pvpConfigs.getPvpLobby()).thenAccept(l -> l.teleport(player));
        }

        if (config.isResetItems()) {
            if (XG7LobbyAPI.customInventoryManager() != null) {
                XG7LobbyAPI.customInventoryManager().closeAllMenus(player);
                XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-pvp-selector-id", String.class).orElse("pvp"), player);
            }
        }

        ActionsProcessor.process(config.getActions(), player);

        Bukkit.getPluginManager().callEvent(new PlayerRespawnInPVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.isEnabled();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if (!XG7LobbyAPI.globalPVPManager().isInPVP(player)) return;
        handle(player);
    }
}
