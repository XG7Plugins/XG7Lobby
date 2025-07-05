package com.xg7plugins.xg7lobby.pvp;

import com.xg7plugins.events.Listener;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.xg7lobby.pvp.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GlobalPVPManager implements Manager {

    private final List<UUID> playersInPVP = new ArrayList<>();

    private static final HashMap<Class<? extends PVPHandler>, PVPHandler> handlers = new HashMap<>();

    static {
        handlers.put(JoinPVPHandler.class, new JoinPVPHandler());
        handlers.put(KillPVPHandler.class, new KillPVPHandler());
        handlers.put(RespawnPVPHandler.class, new RespawnPVPHandler());
        handlers.put(LeavePVPHandler.class, new LeavePVPHandler());
    }


    public void addPlayer(Player player) {
        this.playersInPVP.add(player.getUniqueId());
        getHandler(JoinPVPHandler.class).handle(player);
    }

    public void removePlayer(Player player) {
        this.playersInPVP.remove(player.getUniqueId());
    }

    public boolean isInPVP(Player player) {
        return this.playersInPVP.contains(player.getUniqueId());
    }

    public <T extends PVPHandler> T getHandler(Class<T> clazz) {
        return (T) handlers.get(clazz);
    }

    public List<Player> getAllPlayersInPVP() {
        return playersInPVP.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public List<Listener>  getAllListenersHandlers() {
        return handlers.values().stream().filter(h -> h instanceof Listener).map(h -> (Listener) h).collect(Collectors.toList());
    }





}
