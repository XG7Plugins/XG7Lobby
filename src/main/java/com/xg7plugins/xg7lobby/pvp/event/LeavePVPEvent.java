package com.xg7plugins.xg7lobby.pvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class LeavePVPEvent extends PVPEvent {

    public LeavePVPEvent(@NotNull Player who) {
        super(who);
    }

}
