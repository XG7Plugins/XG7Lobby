package com.xg7plugins.xg7lobby;

import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.xg7lobby.lobby.location.LobbyLocation;
import com.xg7plugins.xg7lobby.lobby.location.LobbyManager;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.lobby.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XG7LobbyAPI {

    public static LobbyPlayerManager lobbyPlayerManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), LobbyPlayerManager.class);
    }

    public static LobbyManager lobbyManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), LobbyManager.class);
    }

    public static CompletableFuture<LobbyPlayer> requestLobbyPlayer(UUID uuid) {
        return lobbyPlayerManager().getPlayer(uuid);
    }

    public static CompletableFuture<LobbyLocation> requestLobbyLocation(String id) {
        return lobbyManager().getLobbyLocation(id);
    }

    public static CompletableFuture<LobbyLocation> requestRandomLobbyLocation() {
        return lobbyManager().getRandomLobbyLocation();
    }

    public static CompletableFuture<List<LobbyLocation>> requestAllLobbyLocations() {
        return lobbyManager().getAllLobbies();
    }

    public static CustomInventoryManager customInventoryManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), CustomInventoryManager.class);
    }

    public static LobbyGUI getCustomGUI(String id) {
        return customInventoryManager().getGUI(id);
    }
    public static LobbyHotbar getCustomHotbar(String id) {
        return customInventoryManager().getHotbar(id);
    }

}
