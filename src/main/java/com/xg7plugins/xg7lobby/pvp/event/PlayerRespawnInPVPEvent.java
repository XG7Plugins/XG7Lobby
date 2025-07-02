package com.xg7plugins.xg7lobby.pvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerRespawnInPVPEvent extends PVPEvent {
    public PlayerRespawnInPVPEvent(@NotNull Player who) {
        super(who);
    }
}
