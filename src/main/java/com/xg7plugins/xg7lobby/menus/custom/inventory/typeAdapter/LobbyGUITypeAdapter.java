package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LobbyGUITypeAdapter implements ConfigTypeAdapter<LobbyGUI> {
    @Override
    public LobbyGUI fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String id = config.get("id");

        String title = config.get("title");

        int rows = config.get("rows");

        List<MenuAction> allowedActions = CustomInventoryManager.parseMenuActions(config, "allowed-menu-actions");
        List<MenuAction> deniedActions = CustomInventoryManager.parseMenuActions(config, "denied-menu-actions");

        long updateDelay = config.getTimeInMilliseconds("update-delay", -1L);

        if (rows == 0 || id == null || title == null) throw new IllegalArgumentException("Invalid config for LobbyGUI!");

        XMaterial fillItem = config.get("fill-material", XMaterial.AIR);

        HashMap<String, LobbyItem> items = new HashMap<>();
        HashMap<Integer, String> grid = new HashMap<>();

        List<List<String>> slots = (List<List<String>>) config.getConfig().getList("grid");

        if (slots != null) {
            int index = 0;
            for (List<String> rowsList : slots) {
                for (String slot : rowsList) {
                    if (slot.isEmpty()) {
                        index++;
                        continue;
                    }
                    grid.put(index, slot);
                    index++;
                }
            }
        }

        ConfigSection itemsSection = config.child("items");

        if (itemsSection.exists()) {
            for (String key : itemsSection.getKeys(false)) {
                items.put(key, itemsSection.child(key).get("", new LobbyItem(Item.air(), key, null, null, Collections.emptyList(), Collections.emptyList())));
            }
        }

        return new LobbyGUI(config.getFile(), id, title, rows, fillItem, items, grid, allowedActions, deniedActions, updateDelay);

    }


    @Override
    public Class<LobbyGUI> getTargetType() {
        return LobbyGUI.class;
    }
}
