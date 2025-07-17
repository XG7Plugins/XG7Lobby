package com.xg7plugins.xg7lobby.data.location;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.managers.Manager;
import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class LobbyManager implements Manager {

    @Getter
    private final LobbyLocationRepository lobbyLocationRepository;


    public LobbyManager() {
        this.lobbyLocationRepository = new LobbyLocationRepository();
    }

    public CompletableFuture<Boolean> saveLobbyLocation(LobbyLocation lobbyLocation) throws ExecutionException, InterruptedException {
        return this.lobbyLocationRepository.addAsync(lobbyLocation);
    }

    public CompletableFuture<Boolean> deleteLobbyLocation(LobbyLocation lobbyLocation) {
        return this.lobbyLocationRepository.deleteAsync(lobbyLocation);
    }

    public CompletableFuture<Boolean> updateLobbyLocation(LobbyLocation lobbyLocation) {
        return this.lobbyLocationRepository.updateAsync(lobbyLocation);
    }

    public CompletableFuture<List<LobbyLocation>> getAllLobbies() {
        return this.lobbyLocationRepository.getAllAsync();
    }

    public CompletableFuture<LobbyLocation> getLobbyLocation(String name) {
        return this.lobbyLocationRepository.getAsync(name);
    }

    public CompletableFuture<LobbyLocation> getRandomLobbyLocation() {
        return CompletableFuture.supplyAsync(() -> {

            List<LobbyLocation> cachedLobbies = XG7PluginsAPI.database().getCachedEntities().asMap().join().values().stream().filter(entity -> entity instanceof LobbyLocation).map(entity -> (LobbyLocation) entity).collect(Collectors.toList());

            List<LobbyLocation> cachedLocalLobbies = cachedLobbies.stream().filter(location -> location.getServerInfo().equals(XG7PluginsAPI.getServerInfo())).collect(Collectors.toList());

            if (!cachedLocalLobbies.isEmpty()) return cachedLocalLobbies.get(new Random().nextInt(cachedLocalLobbies.size()));

            List<LobbyLocation> lobbies = lobbyLocationRepository.getAll();

            if (lobbies.isEmpty()) return null;

            List<LobbyLocation> localLobbies = lobbies.stream().filter(location -> location.getServerInfo().equals(XG7PluginsAPI.getServerInfo())).collect(Collectors.toList());

            if (!localLobbies.isEmpty()) return localLobbies.get(new Random().nextInt(localLobbies.size()));

            return lobbies.get(new Random().nextInt(lobbies.size()));

        });
    }

}
