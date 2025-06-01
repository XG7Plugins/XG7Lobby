package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import lombok.Getter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LobbyPlayerManager implements Manager {

    @Getter
    private final LobbyPlayerDAO lobbyPlayerDAO;

    public LobbyPlayerManager() {
        this.lobbyPlayerDAO = new LobbyPlayerDAO();
    }

    public CompletableFuture<Boolean> savePlayer(LobbyPlayer lobbyPlayer) throws ExecutionException, InterruptedException {
        return this.lobbyPlayerDAO.add(lobbyPlayer);
    }

    public CompletableFuture<Boolean> deletePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.delete(lobbyPlayer);
    }

    public CompletableFuture<Boolean> updatePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.update(lobbyPlayer);
    }

    public CompletableFuture<LobbyPlayer> getPlayer(UUID uuid) {
        return this.lobbyPlayerDAO.get(uuid);
    }

    public boolean isLoaded(UUID id) {
        return XG7PluginsAPI.database().containsCachedEntity(XG7Lobby.getInstance(), id.toString()).join();
    }

    public CompletableFuture<Void> addInfraction(Infraction infraction, UUID playerUUID) {
        return CompletableFuture.runAsync(() -> {

            LobbyPlayer lobbyPlayer = getPlayer(playerUUID).join();

            if (lobbyPlayer == null) return;

            lobbyPlayer.addInfraction(infraction);

            lobbyPlayer.applyInfractions();

            updatePlayer(lobbyPlayer).join();

        });
    }

    public void banPlayer(OfflinePlayer player, Time time, Text reason) {
        if (player.isOnline()) kickPlayer(player.getPlayer(), reason);

        if (time == null) {
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason.getText(), null, null);
            if (player.isOnline()) player.getPlayer().kickPlayer(reason.getText());
        }

        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason.getText(), time.toDate(), null);

    }

    public void banIpPlayer(Player player, Time time, Text reason) {
        kickPlayer(player, reason);

        if (time == null) {
            Bukkit.getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(), reason.getText(), null, null);
            if (player.isOnline()) player.getPlayer().kickPlayer(reason.getText());
        }

        Bukkit.getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(), reason.getText(), time.toDate(), null);
    }

    public void kickPlayer(Player player, Text reason) {
        player.getPlayer().kickPlayer(reason.getText());
    }

    public void mutePlayer(LobbyPlayer player, Time time, Text reason) {
        player.setMuted(true);

        player.setUnmuteTime(time == null ? Time.of(0) : time);

        if (player.getOfflinePlayer().isOnline()) reason.send(player.getPlayer());

       updatePlayer(player).join();
    }
}
