package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.utils.text.Condition;
import com.xg7plugins.utils.text.Text;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface LobbyInventory {

    HashMap<String, LobbyItem> items();
    HashMap<Integer, String> grid();

    BasicMenu getMenu();

    default LobbyItem getItem(Player player, String path) {
        LobbyItem lobbyItem = items().get(path);

        if (lobbyItem == null) return null;

        if (lobbyItem.getConditionLine() == null || lobbyItem.getConditionLine().isEmpty()) return lobbyItem;

        Text transletedText = Text.format(lobbyItem.getConditionLine() + "RANDOLAS").textFor(player);

        if (!transletedText.isEmpty()) return lobbyItem;

        if (lobbyItem.getOtherItemPath() == null) return null;

        return getItem(player, lobbyItem.getOtherItemPath());
    }

}
