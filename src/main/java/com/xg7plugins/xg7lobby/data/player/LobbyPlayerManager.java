package com.xg7plugins.xg7lobby.data.player;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.dao.InfractionDAO;
import com.xg7plugins.xg7lobby.data.player.dao.LobbyPlayerDAO;
import lombok.Getter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LobbyPlayerManager implements Manager {

    @Getter
    private final LobbyPlayerDAO lobbyPlayerDAO;
    @Getter
    private final InfractionDAO infractionDAO;

    public LobbyPlayerManager() {
        this.lobbyPlayerDAO = new LobbyPlayerDAO();
        this.infractionDAO = new InfractionDAO();
    }

    public CompletableFuture<Boolean> savePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.addAsync(lobbyPlayer);
    }

    public CompletableFuture<Boolean> deletePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.deleteAsync(lobbyPlayer);
    }

    public CompletableFuture<Boolean> updatePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.updateAsync(lobbyPlayer);
    }

    public CompletableFuture<LobbyPlayer> getPlayerAsync(UUID uuid) {
        return this.lobbyPlayerDAO.getAsync(uuid);
    }

    public boolean savePlayer(LobbyPlayer lobbyPlayer) throws Exception {
        return this.lobbyPlayerDAO.add(lobbyPlayer);
    }

    public boolean deletePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.delete(lobbyPlayer);
    }

    public boolean updatePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerDAO.update(lobbyPlayer);
    }

    public LobbyPlayer getPlayer(UUID uuid) {
        return this.lobbyPlayerDAO.get(uuid);
    }

    public boolean isLoaded(UUID id) {
        return XG7PluginsAPI.database().containsCachedEntity(XG7Lobby.getInstance(), id.toString()).join();
    }

    public void addInfraction(Infraction infraction) {

        LobbyPlayer lobbyPlayer = getPlayer(infraction.getPlayerUUID());

        if (lobbyPlayer == null) return;

        lobbyPlayer.addInfraction(infraction);

        lobbyPlayer.applyInfractions();

        updatePlayer(lobbyPlayer);
    }

    public Infraction getInfraction(String id) {
        return infractionDAO.get(id);
    }

    public void deleteInfraction(Infraction infraction) {
        infractionDAO.delete(infraction);
    }

    public CompletableFuture<Boolean> saveInfractionAsync(Infraction infraction) {
        return this.infractionDAO.addAsync(infraction);
    }

    public CompletableFuture<Boolean> deleteInfractionAsync(Infraction infraction) {
        return this.infractionDAO.deleteAsync(infraction);
    }

    public List<Infraction> getInfractionsByPlayer(UUID playerUUID) {
        LobbyPlayer lobbyPlayer = getPlayer(playerUUID);

        return lobbyPlayer.getInfractions();
    }

    public List<Infraction> getAllInfractions() {
        return infractionDAO.getAll();
    }

    public void banPlayer(OfflinePlayer player, Time time, Text reason) {
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(player.getName())) return;
        if (player.isOnline()) kickPlayer(player.getPlayer(), reason);

        if (time == null) {
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason.getText(), null, null);
            if (player.isOnline()) player.getPlayer().kickPlayer(reason.getText());
            return;
        }

        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason.getText(), time.isZero() ? null : time.toDate(), null);

    }

    public void banIpPlayer(Player player, Time time, Text reason) {

        if (Bukkit.getBanList(BanList.Type.IP).isBanned(player.getAddress().getAddress().getHostAddress())) return;


        kickPlayer(player, reason);

        if (time == null) {
            Bukkit.getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(), reason.getText(), null, null);
            if (player.isOnline()) player.getPlayer().kickPlayer(reason.getText());
            return;
        }

        Bukkit.getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(), reason.getText(), time.toDate(), null);
    }

    public void kickPlayer(Player player, Text reason) {
        player.getPlayer().kickPlayer(reason.getText());
    }

    public void mutePlayer(LobbyPlayer player, Time time, Text reason) {
        if (player.isMuted()) return;
        player.setMuted(true);

        player.setUnmuteTime(time == null ? Time.of(0) : time);

        if (player.getOfflinePlayer().isOnline()) reason.send(player.getPlayer());

       updatePlayer(player);
    }
}
