package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.cryptomorin.xseries.XMaterial;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;

public class LobbyGUITypeAdapter implements ConfigTypeAdapter<LobbyGUI> {
    @Override
    public LobbyGUI fromConfig(Config config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class).orElseThrow(() -> new IllegalArgumentException("id is required"));

        String title = config.get("title", String.class).orElseThrow(() -> new IllegalArgumentException("title is required"));

        int rows = config.get("rows", Integer.class).orElseThrow(() -> new IllegalArgumentException("rows is required"));

        XMaterial fillItem = config.get("fill-material", XMaterial.class, true).orElse(XMaterial.AIR);

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

        ConfigurationSection itemsSection = config.get("items", ConfigurationSection.class).orElse(null);

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                items.put(key, config.get("items." + key, LobbyItem.class).orElse(new LobbyItem(Item.air(), null, null)));
            }
        }

        return new LobbyGUI(config, id, title, rows, fillItem, items, grid);

    }

    @Override
    public Class<LobbyGUI> getTargetType() {
        return LobbyGUI.class;
    }
}
