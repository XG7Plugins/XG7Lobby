package com.xg7plugins.xg7lobby.plugin;

import com.xg7plugins.api.API;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.location.LobbyLocationManager;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.holograms.HologramsManager;
import com.xg7plugins.xg7lobby.menus.custom.forms.CustomFormsManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.menus.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.menus.LobbyHotbar;
import com.xg7plugins.xg7lobby.npcs.NPCsManager;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import com.xg7plugins.xg7lobby.queue.QueueManager;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class XG7LobbyAPI implements API<XG7Lobby> {

    private final XG7Lobby plugin;

    public XG7LobbyAPI(XG7Lobby plugin) {
        this.plugin = plugin;
    }

    public LobbyPlayerManager lobbyPlayerManager() {
        return plugin.getLobbyPlayerManager();
    }

    public LobbyLocationManager lobbyManager() {
        return plugin.getLobbyManager();
    }

    public CompletableFuture<LobbyPlayer> requestLobbyPlayer(UUID uuid) {
        return lobbyPlayerManager().getPlayerAsync(uuid);
    }

    public LobbyPlayer getLobbyPlayer(UUID uuid) {
        return lobbyPlayerManager().getPlayer(uuid);
    }

    public CompletableFuture<LobbyLocation> requestLobbyLocation(String id) {
        return lobbyManager().getLobbyLocation(id);
    }

    public CompletableFuture<LobbyLocation> requestRandomLobbyLocation() {
        return lobbyManager().getRandomLobbyLocation();
    }

    public CompletableFuture<List<LobbyLocation>> requestAllLobbyLocations() {
        return lobbyManager().getAllLobbies();
    }

    public CustomInventoryManager customInventoryManager() {
        return plugin.getCustomInventoryManager();
    }

    public CustomFormsManager customFormsManager() {
        return plugin.getCustomFormsManager();
    }

    public LobbyGUI getCustomGUI(String id) {
        return customInventoryManager().getGUI(id);
    }
    public LobbyHotbar getCustomHotbar(String id) {
        return customInventoryManager().getHotbar(id);
    }

    public GlobalPVPManager globalPVPManager() {
        return plugin.getGlobalPVPManager();
    }

    public HologramsManager hologramsManager() {
        return plugin.getHologramsManager();
    }

    public NPCsManager npcsManager() {
        return plugin.getNpcsManager();
    }

    public QueueManager queuesManager() {
        return plugin.getQueueManager();
    }

    public boolean isPlayerInPVP(Player player) {
        return globalPVPManager().isInPVP(player);
    }

}
