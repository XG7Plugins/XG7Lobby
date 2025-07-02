package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.pvp.event.JoinPVPEvent;
import com.xg7plugins.xg7lobby.pvp.event.LeavePVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class LeavePVPHandler implements PVPHandler {
    @Override
    public void handle(Player player) {

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-leave");

        XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

        XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-selector-id", String.class).orElse("selector"), player);

        Config config = Config.of("pvp", XG7Lobby.getInstance());

        ActionsProcessor.process(config.getList("on-leave-pvp.actions", String.class).orElse(Collections.emptyList()), player);

        Bukkit.getPluginManager().callEvent(new LeavePVPEvent(player));

    }
}
