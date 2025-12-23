package com.xg7plugins.xg7lobby.pvp.event;

import com.xg7plugins.xg7lobby.plugin.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class PVPEvent extends PlayerEvent {

    protected static final HandlerList HANDLERS = new HandlerList();

    public PVPEvent(@NotNull Player who) {
        super(who);
    }

    public LobbyPlayer getLobbyPlayer() {
        return XG7LobbyAPI.getLobbyPlayer(player.getUniqueId());
    }

    public CompletableFuture<LobbyPlayer> requestLobbyPlayer() {
        return XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId());
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }



}
