package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.google.common.collect.Lists;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.holders.PlayerMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menus.player.PlayerMenu;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomInventoryManager implements Manager {

    private final HashMap<String, LobbyInventory> inventories = new HashMap<>();

    @Getter
    private boolean enabled;

    public void loadInventories() {
        XG7Lobby lobby = XG7Lobby.getInstance();

        enabled = Config.mainConfigOf(lobby).get("menus-enabled", Boolean.class).orElse(true);

        lobby.getDebug().loading("Loading custom inventories...");

        File folderFile = new File(lobby.getDataFolder(), "menus");
        File guiFolder = new File(lobby.getDataFolder(), "menus/gui");
        File hotbarFolder = new File(lobby.getDataFolder(), "menus/hotbar");

        boolean existsBefore = folderFile.exists();

        if (!existsBefore) {
            guiFolder.mkdirs();
            hotbarFolder.mkdirs();
        }

        List<File> guis = getDefaults(existsBefore, Arrays.asList("games", "profile"), guiFolder, "gui");
        List<File> hotbars = getDefaults(existsBefore, Arrays.asList("selector", "pvp_selector"), hotbarFolder, "hotbar");

        for (File file : guis) {
            Config config = Config.of("menus/gui/" + file.getName().replace(".yml", ""), lobby);

            String id = config.get("id", String.class).orElse(null);

            config.get("", LobbyGUI.class).ifPresent((inv) -> {
                inventories.put(id, inv);
                XG7Menus.getInstance().registerMenus(inv);
            });
        }
        for (File file : hotbars) {
            Config config = Config.of("menus/hotbar/" + file.getName().replace(".yml", ""), lobby);

            String id = config.get("id", String.class).orElse(null);

            config.get("", LobbyHotbar.class).ifPresent((inv) -> {
                inventories.put(id, inv);
                XG7Menus.getInstance().registerMenus(inv);
            });
        }
        XG7Lobby.getInstance().getDebug().loading("Loaded " + inventories.size() + " custom inventories.");
        XG7Lobby.getInstance().getDebug().loading("Loaded " + inventories.keySet() + " custom inventories.");

    }

    private List<File> getDefaults(boolean existsBefore, List<String> invs, File folder, String path) {

        List<File> invsFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(folder.listFiles())));

        for (String inv : invs) {
            File file = new File(folder, "menus/" + path + "/" + inv + ".yml");
            if (!file.exists() && !existsBefore) {
                XG7Lobby.getInstance().saveResource("menus/" + path + "/" + inv + ".yml", false);
                invsFiles.add(file);
            }
        }
        return invsFiles;
    }

    public LobbyInventory getInventory(String id) {
        return inventories.get(id);
    }

    public <T extends LobbyInventory> T getInventory(String id, Class<T> clazz) {
        if (!inventories.containsKey(id)) return null;
        return clazz.cast(inventories.get(id));
    }

    public LobbyHotbar getHotbar(String id) {
        return getInventory(id, LobbyHotbar.class);
    }
    public LobbyGUI getGUI(String id) {
        return getInventory(id, LobbyGUI.class);
    }

    public LobbyHotbar getHotbarByXG7MenusId(String id) {
        for (LobbyInventory inv : inventories.values()) {
            if (inv instanceof LobbyHotbar && inv.getMenu().getMenuConfigs().getId().equalsIgnoreCase(id)) return (LobbyHotbar) inv;
        }
        return null;
    }

    public LobbyGUI getGUIByXG7MenusId(String id) {
        for (LobbyInventory inv : inventories.values()) {
            if (inv instanceof LobbyGUI && inv.getMenu().getMenuConfigs().getId().equalsIgnoreCase(id)) return (LobbyGUI) inv;
        }
        return null;
    }

    public boolean isGUI(String id) {
        return getInventory(id) instanceof LobbyGUI;
    }

    public boolean isHotbar(String id) {
        return getInventory(id) instanceof LobbyHotbar;
    }

    public Collection<LobbyInventory> getInventories() {
        return inventories.values();
    }

    public void openMenu(String id, Player player) {
        if (!inventories.containsKey(id)) return;
        LobbyInventory lobbyInventory = inventories.get(id);


        if (lobbyInventory instanceof LobbyHotbar) {
            PlayerMenuHolder playerMenu = XG7Menus.getPlayerMenuHolder(player.getUniqueId());
            if (playerMenu != null) playerMenu.getMenu().close(playerMenu);
        }

        lobbyInventory.getMenu().open(player);
    }

    public void closeAllMenus(Player player) {
        if (!enabled) return;
        player.closeInventory();
        if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
            PlayerMenuHolder holder = XG7Menus.getPlayerMenuHolder(player.getUniqueId());
            holder.getMenu().close(holder);
        }
    }

    public List<String> getIds() {
        return new ArrayList<>(inventories.keySet());
    }

    public void reloadInventories() {
        inventories.clear();
        loadInventories();
    }




}
