package com.xg7plugins.xg7lobby.pvp;

import com.xg7plugins.managers.Manager;
import com.xg7plugins.xg7lobby.pvp.handlers.JoinPVPHandler;
import com.xg7plugins.xg7lobby.pvp.handlers.PVPHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalPVPManager implements Manager {

    private final List<UUID> playersInPVP = new ArrayList<>();

    private static final HashMap<Class<? extends PVPHandler>, PVPHandler> handlers = new HashMap<>();

    static {
        handlers.put(JoinPVPHandler.class, new JoinPVPHandler());
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





}
