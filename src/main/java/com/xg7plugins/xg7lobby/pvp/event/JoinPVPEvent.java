package com.xg7plugins.xg7lobby.pvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class JoinPVPEvent extends PVPEvent {

    public JoinPVPEvent(@NotNull Player who) {
        super(who);
    }


}
