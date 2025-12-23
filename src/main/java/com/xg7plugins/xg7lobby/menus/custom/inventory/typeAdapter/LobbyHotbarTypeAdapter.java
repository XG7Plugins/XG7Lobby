package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;

import java.util.*;

public class LobbyHotbarTypeAdapter implements ConfigTypeAdapter<LobbyHotbar> {
    @Override
    public LobbyHotbar fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String id = config.get("id");

        List<MenuAction> allowedActions = CustomInventoryManager.parseMenuActions(config, "allowed-menu-actions");
        List<MenuAction> deniedActions = CustomInventoryManager.parseMenuActions(config, "denied-menu-actions");

        long updateDelay = config.getTimeInMilliseconds("update-delay", -1L);

        if (id == null) throw  new NullPointerException("id is required");

        HashMap<String, LobbyItem> items = new HashMap<>();
        HashMap<Integer, String> grid = new HashMap<>();

        List<String> row = config.getList("row", String.class).orElse(new ArrayList<>());

        for (int i = 0; i < row.size(); i++) {
            grid.put(i, row.get(i));
        }

        ConfigSection itemsSection = config.child("items");

        if (itemsSection.exists()) {
            for (String key : itemsSection.getKeys(false)) {
                items.put(key, itemsSection.child(key).get("", new LobbyItem(Item.air(), key, null, null, allowedActions, deniedActions)));
            }
        }

        Time cooldown = config.getTimeOrDefault("cooldown-to-use", Time.of(2));
        boolean disableCooldown = config.get("disable-cooldown", false);

        return new LobbyHotbar(config.getFile(), id, items, grid, cooldown, disableCooldown, allowedActions, deniedActions, updateDelay);

    }

    @Override
    public Class<LobbyHotbar> getTargetType() {
        return LobbyHotbar.class;
    }

}
