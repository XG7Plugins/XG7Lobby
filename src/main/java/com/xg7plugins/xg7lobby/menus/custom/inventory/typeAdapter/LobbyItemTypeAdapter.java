package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class LobbyItemTypeAdapter implements ConfigTypeAdapter<LobbyItem> {
    @Override
    public LobbyItem fromConfig(Config config, String path, Object... optionalArgs) {

        String material = config.get(path + ".material", String.class).orElse("AIR");
        int amount = config.get(path + ".amount", Integer.class).orElse(1);
        String name = config.get(path + ".name", String.class).orElse("No name");
        List<String> lore = config.getList(path + ".lore", String.class, true).orElse(new ArrayList<>());
        List<String> enchants = config.getList(path + ".enchants", String.class, true).orElse(new ArrayList<>());
        List<String> actions = config.getList(path + ".actions", String.class, true).orElse(new ArrayList<>());

        Item item = Item.from(material)
                .amount(amount)
                .name(name)
                .lore(lore)
                .setNBTTag("actions", actions);

        for (String enchant : enchants) {
            Enchantment enchantment = Enchantment.getByName(enchant.split(", ")[0]);
            if (enchantment == null) continue;
            item.enchant(enchantment, Integer.parseInt(enchant.split(", ")[1]));
        }

        String conditionLine = config.get(path + ".conditional", String.class, true).orElse(null);

        String otherItemPath = config.get(path + ".if-false", String.class, true).orElse(null);

        return new LobbyItem(item, conditionLine, otherItemPath);
    }

    @Override
    public Class<LobbyItem> getTargetType() {
        return LobbyItem.class;
    }
}
