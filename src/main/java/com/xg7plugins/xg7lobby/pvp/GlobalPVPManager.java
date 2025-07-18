package com.xg7plugins.xg7lobby.pvp;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.handlers.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalPVPManager implements Manager {

    private final Set<UUID> playersInPVP = new HashSet<>();

    private final HashMap<Class<? extends PVPHandler>, PVPHandler> handlers = new HashMap<>();

    @Getter
    private final CombatLogHandler combatLogHandler;

    {
        handlers.put(JoinPVPHandler.class, new JoinPVPHandler());
        handlers.put(KillPVPHandler.class, new KillPVPHandler());
        handlers.put(RespawnPVPHandler.class, new RespawnPVPHandler());
        handlers.put(LeavePVPHandler.class, new LeavePVPHandler());

        combatLogHandler = new CombatLogHandler();
    }

    public void addPlayer(Player player) {
        Debug.of(XG7Lobby.getInstance()).info("Adding " + player.getName() + " to lobby pvp");
        this.playersInPVP.add(player.getUniqueId());
        getHandler(JoinPVPHandler.class).handle(player);
    }

    public void removePlayer(Player player) {
        if (!playersInPVP.contains(player.getUniqueId())) return;

        Debug.of(XG7Lobby.getInstance()).info("Removing " + player.getName() + " from lobby pvp");

        this.playersInPVP.remove(player.getUniqueId());
        getHandler(LeavePVPHandler.class).handle(player);
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

    public List<Listener> getAllListenersHandlers() {
        List<Listener> listeners = new ArrayList<>(handlers.values().stream().map(l -> (Listener) l).collect(Collectors.toList()));
       listeners.add(combatLogHandler);
       return listeners;
    }


}
