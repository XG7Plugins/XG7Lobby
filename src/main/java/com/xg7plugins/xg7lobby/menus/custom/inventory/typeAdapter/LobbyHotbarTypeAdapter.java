package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LobbyHotbarTypeAdapter implements ConfigTypeAdapter<LobbyHotbar> {
    @Override
    public LobbyHotbar fromConfig(Config config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class).orElseThrow(() -> new IllegalArgumentException("id is required"));

        HashMap<String, LobbyItem> items = new HashMap<>();
        HashMap<Integer, String> grid = new HashMap<>();

        List<String> row = config.getList("row", String.class).orElse(new ArrayList<>());

        for (int i = 0; i < row.size(); i++) {
            grid.put(i, row.get(i));
        }

        ConfigurationSection itemsSection = config.get("items", ConfigurationSection.class).orElse(null);

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                items.put(key, config.get("items." + key, LobbyItem.class).orElse(new LobbyItem(Item.air(), null, null)));
            }
        }

        return new LobbyHotbar(config, id, items, grid, config.getTime("cooldown").orElse(Time.of(2)));

    }

    @Override
    public Class<LobbyHotbar> getTargetType() {
        return LobbyHotbar.class;
    }
}
