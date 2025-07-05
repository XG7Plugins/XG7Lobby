package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.configs.XG7LobbyEnvironment;
import com.xg7plugins.xg7lobby.pvp.event.JoinPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;

public class JoinPVPHandler implements PVPHandler {

    @Override
    public void handle(Player player, Object... args) {

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-join");

        PVPConfigs.OnJoinPvp config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnJoinPvp.class);
        PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        player.setGameMode(GameMode.SURVIVAL);
        Config.of(XG7Lobby.getInstance(), PlayerConfigs.class).reset(player);

        XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

        XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-pvp-selector-id", String.class).orElse("pvp"), player);

        if (config.isHeal()) player.setHealth(player.getMaxHealth());

        if (config.isTeleportToPvpLobby()) {
            XG7LobbyAPI.requestLobbyLocation(pvpConfigs.getPvpLobby()).thenAccept(l -> l.teleport(player));
        }

        ActionsProcessor.process(config.getActions(), player);

        Bukkit.getPluginManager().callEvent(new JoinPVPEvent(player));

    }

}
