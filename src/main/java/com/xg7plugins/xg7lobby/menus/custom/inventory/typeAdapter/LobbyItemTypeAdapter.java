package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;


import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class LobbyItemTypeAdapter implements ConfigTypeAdapter<LobbyItem> {
    @Override
    public LobbyItem fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String material = config.get("material", "AIR");
        int amount = config.get("amount", 1);
        String name = config.get("name", "No name");
        List<String> lore = config.getList("lore", String.class).orElse(new ArrayList<>());
        List<String> enchants = config.getList("enchants", String.class).orElse(new ArrayList<>());
        List<ItemFlag> itemFlags = config.getList("item-flags", ItemFlag.class).orElse(new ArrayList<>());

        List<MenuAction> allowedActions = CustomInventoryManager.parseMenuActions(config, "allowed-menu-actions");
        List<MenuAction> deniedActions = CustomInventoryManager.parseMenuActions(config, "denied-menu-actions");

        List<String> actions = config.getList("actions", String.class).orElse(new ArrayList<>());

        String configPath = config.getPath();

        String id = configPath.split("\\.")[configPath.split("\\.").length - 1];

        if (config.child("material-settings").exists()) {
            String type = config.child("material-settings").get("type");

            try {
                MaterialSettings settings = MaterialSettings.valueOf(type.toUpperCase());

                material += settings.getSettings(config.child("material-settings"));

            } catch (Exception ig) {
                XG7Lobby.getInstance().getDebug().warn("menus", "Invalid material settings type: " + type + ". Using default material settings");
            }
        }

        Item item = Item.from(material)
                .amount(amount)
                .name(name)
                .lore(lore)
                .flags(itemFlags.toArray(new ItemFlag[0]))
                .setNBTTag("lobby-item_id", id)
                .setNBTTag("lobby-item_actions", actions);

        for (String enchant : enchants) {
            Enchantment enchantment = Enchantment.getByName(enchant.split(", ")[0]);
            if (enchantment == null) continue;
            item.enchant(enchantment, Integer.parseInt(enchant.split(", ")[1]));
        }

        String conditionLine = config.get("conditional");

        String otherItemPath = config.get("if-false");

        return new LobbyItem(item, id, conditionLine, otherItemPath, allowedActions, deniedActions);
    }

    @Override
    public Class<LobbyItem> getTargetType() {
        return LobbyItem.class;
    }

}
