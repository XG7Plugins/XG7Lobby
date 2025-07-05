package com.xg7plugins.xg7lobby.pvp.event;

import com.xg7plugins.xg7lobby.pvp.DeathCause;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerKillInPVPEvent extends PVPEvent {

    private final Player killer;
    private final Player victim;
    private final DeathCause killCause;

    public PlayerKillInPVPEvent(Player killer, @NotNull Player victim, DeathCause killCause) {
        super(victim);
        this.killer = killer;
        this.victim = victim;
        this.killCause = killCause;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
