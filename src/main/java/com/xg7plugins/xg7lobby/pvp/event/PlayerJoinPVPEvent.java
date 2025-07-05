package com.xg7plugins.xg7lobby.pvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinPVPEvent extends PVPEvent {

    public PlayerJoinPVPEvent(@NotNull Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
