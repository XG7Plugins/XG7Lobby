package com.xg7plugins.xg7lobby.data.player;

import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.data.player.dao.InfractionRepository;
import com.xg7plugins.xg7lobby.data.player.dao.LobbyPlayerRepository;
import lombok.Getter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LobbyPlayerManager {

    @Getter
    private final LobbyPlayerRepository lobbyPlayerRepository;
    @Getter
    private final InfractionRepository infractionRepository;

    public LobbyPlayerManager() {
        this.lobbyPlayerRepository = new LobbyPlayerRepository();
        this.infractionRepository = new InfractionRepository();
    }

    public CompletableFuture<Boolean> savePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerRepository.addAsync(lobbyPlayer);
    }

    public CompletableFuture<Boolean> deletePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerRepository.deleteAsync(lobbyPlayer);
    }

    public CompletableFuture<Boolean> updatePlayerAsync(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerRepository.updateAsync(lobbyPlayer);
    }

    public CompletableFuture<LobbyPlayer> getPlayerAsync(UUID uuid) {
        return this.lobbyPlayerRepository.getAsync(uuid);
    }

    public boolean savePlayer(LobbyPlayer lobbyPlayer) throws Exception {
        return this.lobbyPlayerRepository.add(lobbyPlayer);
    }

    public boolean deletePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerRepository.delete(lobbyPlayer);
    }

    public boolean updatePlayer(LobbyPlayer lobbyPlayer) {
        return this.lobbyPlayerRepository.update(lobbyPlayer);
    }

    public LobbyPlayer getPlayer(UUID uuid) {
        return this.lobbyPlayerRepository.get(uuid);
    }

    public boolean isLoaded(UUID id) {
        return XG7Plugins.getAPI().database().containsCachedEntity(XG7Lobby.getInstance(), id.toString()).join();
    }

    public void addInfraction(Infraction infraction) {

        LobbyPlayer lobbyPlayer = getPlayer(infraction.getPlayerUUID());

        if (lobbyPlayer == null) return;

        lobbyPlayer.addInfraction(infraction);

        lobbyPlayer.applyInfractions();

        updatePlayer(lobbyPlayer);
    }

    public Infraction getInfraction(String id) {
        return infractionRepository.get(id);
    }

    public void deleteInfraction(Infraction infraction) {
        infractionRepository.delete(infraction);
    }

    public CompletableFuture<Boolean> saveInfractionAsync(Infraction infraction) {
        return this.infractionRepository.addAsync(infraction);
    }

    public CompletableFuture<Boolean> deleteInfractionAsync(Infraction infraction) {
        return this.infractionRepository.deleteAsync(infraction);
    }

    public List<Infraction> getInfractionsByPlayer(UUID playerUUID) {
        LobbyPlayer lobbyPlayer = getPlayer(playerUUID);

        return lobbyPlayer.getInfractions();
    }

    public List<Infraction> getAllInfractions() {
        return infractionRepository.getAll();
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
