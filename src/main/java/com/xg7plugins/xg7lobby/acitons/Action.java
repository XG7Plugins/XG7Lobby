package com.xg7plugins.xg7lobby.acitons;

import com.xg7plugins.modules.xg7menus.item.InventoryItem;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.text.Condition;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
public class Action {

    private final ActionType actionType;
    private final String conditionLine;
    private final String[] args;

    public void execute(Player player) {
        if (conditionLine != null && Condition.processConditions(Text.format(conditionLine).textFor(player).getPlainText(), player).isEmpty())
            return;
        actionType.execute(player, args);
    }

    public static InventoryItem actionItem(ActionType type) {
        Item item = Item.from(type.getIcon());

        item.name("lang:[help.menu.actions-menu.action-item.name]");
        item.lore(Arrays.asList("lang:[help.menu.actions-menu.action-item.lore.description]", "lang:[help.menu.actions-menu.action-item.lore.usage]"));

        item.setBuildPlaceholders(
                Pair.of("id", type.name().toLowerCase()),
                Pair.of("description", type.getDescription()),
                Pair.of("usage", type.getUsage())
        );

        return item.toInventoryItem(-1, true);
    }
}
