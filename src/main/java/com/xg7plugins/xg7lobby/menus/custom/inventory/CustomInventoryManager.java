package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class CustomInventoryManager implements Manager {

    private final HashMap<String, LobbyInventory> inventories = new HashMap<>();

    public void loadInventories() {
        XG7Lobby lobby = XG7Lobby.getInstance();

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
            ConfigSection config = ConfigFile.of("menus/gui/" + file.getName().replace(".yml", ""), lobby).root();

            String id = config.get("id", String.class);
            if (id == null) throw new IllegalArgumentException("GUI id cannot be null!");

            LobbyGUI lobbyGUI = config.get("", LobbyGUI.class);

            if (lobbyGUI == null) throw new IllegalArgumentException("GUI malconfigured!");

            inventories.put(id, lobbyGUI);
            XG7Menus.getInstance().registerMenus(lobbyGUI);
        }
        for (File file : hotbars) {
            ConfigSection config = ConfigFile.of("menus/hotbar/" + file.getName().replace(".yml", ""), lobby).root();

            String id = config.get("id", String.class);
            if (id == null) throw new IllegalArgumentException("Hotbar id cannot be null!");

            LobbyHotbar lobbyHotbar = config.get("", LobbyHotbar.class);

            if (lobbyHotbar == null) throw new IllegalArgumentException("Hotbar malconfigured!");

            inventories.put(id, lobbyHotbar);
            XG7Menus.getInstance().registerMenus(lobbyHotbar);
        }
        lobby.getDebug().loading("Loaded " + inventories.size() + " custom inventories.");
        lobby.getDebug().info("Loaded " + inventories.keySet() + " custom inventories.");

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

    public boolean isMenu(String id) {
        return inventories.containsKey(id);
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

    public List<String> getIds() {
        return new ArrayList<>(inventories.keySet());
    }

    public void reloadInventories() {
        inventories.values().forEach(l -> XG7Menus.getInstance().unregisterMenu(XG7Lobby.getInstance(), l.getMenu().getMenuConfigs().getId()));
        inventories.clear();
        loadInventories();
    }

    public static List<MenuAction> parseMenuActions(ConfigSection config, String key) {

        if (config.is(key, String.class)) {

            String value = config.get(key,"").trim().toUpperCase();

            if ("NONE".equals(value)) return new ArrayList<>(EnumSet.noneOf(MenuAction.class));
            if ("ALL".equals(value)) return new ArrayList<>(EnumSet.allOf(MenuAction.class));

            try {
                return Collections.singletonList(MenuAction.valueOf(value));
            } catch (IllegalArgumentException e) {
                return new ArrayList<>();
            }
        }

        return config.getList(key, MenuAction.class).orElse(new ArrayList<>());
    }




}
