package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.xg7lobby.events.LobbyListener;
import org.bukkit.entity.Player;

public interface PVPHandler extends LobbyListener {

    void handle(Player player, Object... args);

}
