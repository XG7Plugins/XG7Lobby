package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.pvp.event.PlayerLeavePVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeavePVPHandler implements PVPHandler, LobbyListener {

    private final PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

    @Override
    public void handle(Player player, Object... args) {

        PVPConfigs.OnLeavePvp config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnLeavePvp.class);

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-leave");

        if (player.isOnline()) {

            if (XG7LobbyAPI.customInventoryManager() != null) {
                XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

                XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-selector-id", String.class).orElse("selector"), player);
            }

            Config.of(XG7Lobby.getInstance(), PlayerConfigs.class).apply(player);

            ActionsProcessor.process(config.getActions(), player);

            XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                lobbyPlayer.fly();
                lobbyPlayer.applyBuild();
                lobbyPlayer.applyHide();
            });

        }

        Bukkit.getPluginManager().callEvent(new PlayerLeavePVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.isEnabled();

    }

    @Override
    public void onWorldLeave(Player player, World newWorld) {
        XG7LobbyAPI.globalPVPManager().removePlayer(player);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        XG7LobbyAPI.globalPVPManager().removePlayer(event.getPlayer());
    }
}
