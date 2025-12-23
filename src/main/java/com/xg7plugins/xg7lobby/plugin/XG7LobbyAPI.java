package com.xg7plugins.xg7lobby.plugin;

import com.xg7plugins.managers.ManagerRegistry;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.location.LobbyLocationManager;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayerManager;
import com.xg7plugins.xg7lobby.menus.custom.forms.CustomFormsManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XG7LobbyAPI {

    public static LobbyPlayerManager lobbyPlayerManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), LobbyPlayerManager.class);
    }

    public static LobbyLocationManager lobbyManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), LobbyLocationManager.class);
    }

    public static CompletableFuture<LobbyPlayer> requestLobbyPlayer(UUID uuid) {
        return lobbyPlayerManager().getPlayerAsync(uuid);
    }

    public static LobbyPlayer getLobbyPlayer(UUID uuid) {
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

    public static CustomFormsManager customFormsManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), CustomFormsManager.class);
    }

    public static LobbyGUI getCustomGUI(String id) {
        return customInventoryManager().getGUI(id);
    }
    public static LobbyHotbar getCustomHotbar(String id) {
        return customInventoryManager().getHotbar(id);
    }

    public static GlobalPVPManager globalPVPManager() {
        return ManagerRegistry.get(XG7Lobby.getInstance(), GlobalPVPManager.class);
    }

    public static boolean isPlayerInPVP(Player player) {
        return globalPVPManager().isInPVP(player);
    }

}
