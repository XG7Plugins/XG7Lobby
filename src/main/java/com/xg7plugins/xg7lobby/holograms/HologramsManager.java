package com.xg7plugins.xg7lobby.holograms;

import com.google.gson.reflect.TypeToken;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.json.JsonManager;
import com.xg7plugins.modules.xg7holograms.hologram.Hologram;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class HologramsManager {

    private final HashMap<String, LobbyHologram> holograms = new HashMap<>();

    private final XG7Lobby plugin;

    // Synchronous loading and saving of holograms
    public void loadHolograms() {

        JsonManager jsonManager = XG7Plugins.getAPI().jsonManager();

        TypeToken<List<LobbyHologram>> typeToken = new TypeToken<List<LobbyHologram>>() {};

        List<LobbyHologram> lobbyHolograms = jsonManager.load(plugin, "data/holograms.json", typeToken).join();
        lobbyHolograms.forEach(this::registerHologram);

        XG7Plugins.getAPI().taskManager().runTimerTask(XG7Plugins.getAPI().getTimerTask(XG7Plugins.getPluginID("holograms-task")));
        XG7Plugins.getAPI().taskManager().runTimerTask(XG7Plugins.getAPI().getTimerTask(XG7Plugins.getPluginID("holograms-levitating-task")));
    }

    public CompletableFuture<Void> saveHolograms() {

        JsonManager jsonManager = XG7Plugins.getAPI().jsonManager();

        List<LobbyHologram> lobbyHolograms = new ArrayList<>(holograms.values());

        System.out.println("Saving holograms: ");
        holograms.forEach((key, value) ->
                System.out.println(key + ": " + value)
        );

        return jsonManager.saveJson(plugin, "data/holograms.json", lobbyHolograms);

    }

    public LobbyHologram getHologram(String id) {
        return holograms.get(id);
    }

    public void registerHologram(LobbyHologram lobbyHologram) {
        holograms.put(lobbyHologram.getId(), lobbyHologram);
        Hologram hologram = lobbyHologram.toHologram();
        XG7Plugins.getAPI().holograms().registerHologram(hologram);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player))
                .forEach(hologram::spawn);
    }

    public void unregisterHologram(String id) {
        holograms.remove(id);
        XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(uuid ->
                XG7Plugins.getAPI().holograms().unregisterLivingHologram(uuid, "xg7lobby_hologram-" + id));

        XG7Plugins.getAPI().holograms().unregisterHologram("xg7lobby_hologram-" + id);
    }

    public void unregisterAllHolograms() {
        for (String id : holograms.keySet()) unregisterHologram(id);
        holograms.clear();
    }

    public boolean existsHologram(String id) {
        return holograms.containsKey(id);
    }

    public void respawnHologram(String id) {
        LobbyHologram lobbyHologram = holograms.get(id);
        if (lobbyHologram == null) return;

        XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(uuid ->
                XG7Plugins.getAPI().holograms().unregisterLivingHologram(uuid, "xg7lobby_hologram-" + id));

        Hologram hologram = lobbyHologram.toHologram();
        XG7Plugins.getAPI().holograms().registerHologram(hologram);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player))
                .forEach(hologram::spawn);
    }


}
