package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.utils.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LobbyItem {

    private final Item item;

    private final String id;

    private final String conditionLine;
    private final String otherItemPath;

    private final List<MenuAction> allowedActions;
    private final List<MenuAction> deniedActions;


}
