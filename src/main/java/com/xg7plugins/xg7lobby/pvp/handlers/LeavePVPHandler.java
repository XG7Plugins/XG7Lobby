package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.event.LeavePVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class LeavePVPHandler implements PVPHandler {
    @Override
    public void handle(Player player, Object... args) {

        PVPConfigs.OnLeavePvp config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnLeavePvp.class);

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-leave");

        if (player.isOnline()) {
            XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

            XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-selector-id", String.class).orElse("selector"), player);

            ActionsProcessor.process(config.getActions(), player);

        }

        Bukkit.getPluginManager().callEvent(new LeavePVPEvent(player));

    }
}
