package com.xg7plugins.xg7lobby.npcs;

import com.google.gson.reflect.TypeToken;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.data.json.JsonManager;
import com.xg7plugins.modules.xg7npcs.living.LivingNPC;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//This manager is similar to HologramsManager, but for NPCs
//It's because NPCs and Holograms are entities
@RequiredArgsConstructor
@Getter
public class NPCsManager {

    private final XG7Lobby plugin;

    private final HashMap<String, LobbyNPC> npcs = new HashMap<>();

    // Synchronous loading and saving of holograms
    public void loadNPCs() {

        JsonManager jsonManager = XG7Plugins.getAPI().jsonManager();

        TypeToken<List<LobbyNPC>> typeToken = new TypeToken<List<LobbyNPC>>() {};

        List<LobbyNPC> lobbyNPCS = jsonManager.load(plugin, "data/npcs.json", typeToken).join();
        lobbyNPCS.forEach(this::registerNPC);

        //Looking to player task
        XG7Plugins.getAPI().taskManager().runTimerTask(XG7Plugins.getAPI().getTimerTask(XG7Plugins.getPluginID("npcs-task")));
    }

    public CompletableFuture<Void> saveNPCs() {

        JsonManager jsonManager = XG7Plugins.getAPI().jsonManager();
        List<LobbyNPC> lobbyNPCS = new ArrayList<>(npcs.values());

        return jsonManager.saveJson(plugin, "data/npcs.json", lobbyNPCS);

    }

    public LobbyNPC getNPC(String id) {
        return npcs.get(id);
    }

    public void registerNPC(LobbyNPC lobbyNPC) {
        npcs.put(lobbyNPC.getId(), lobbyNPC);
        NPC npc = lobbyNPC.toNPC();
        XG7Plugins.getAPI().npcs().registerNPC(npc);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player))
                .forEach(npc::spawn);
    }

    public void unregisterNPC(String id) {
        npcs.remove(id);
        XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(uuid ->
                XG7Plugins.getAPI().npcs().unregisterLivingNPC(uuid, "xg7lobby_npc-" + id));
        XG7Plugins.getAPI().npcs().unregisterNPC("xg7lobby_npc-" + id);
    }

    public void unregisterAllNPCs() {
        for (String id : npcs.keySet()) unregisterNPC(id);
        npcs.clear();
    }

    public boolean existsNPC(String id) {
        return npcs.containsKey(id);
    }

    public void respawnNPC(String id) {
        LobbyNPC lobbyNPC = npcs.get(id);
        if (lobbyNPC == null) return;

        XG7Plugins.getAPI().getAllPlayerUUIDs().forEach(uuid ->
                XG7Plugins.getAPI().npcs().unregisterLivingNPC(uuid, "xg7lobby_npc-" + id));

        NPC npc = lobbyNPC.toNPC();
        XG7Plugins.getAPI().npcs().registerNPC(npc);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> XG7Plugins.getAPI().isInAnEnabledWorld(XG7Lobby.getInstance(), player))
                .forEach(npc::spawn);
    }

    public LivingNPC getSpawnedNPC(String id, Player player) {
        return XG7Plugins.getAPI().npcs().getLivingNPC(player.getUniqueId(), "xg7lobby_npc-" + id);
    }

}
