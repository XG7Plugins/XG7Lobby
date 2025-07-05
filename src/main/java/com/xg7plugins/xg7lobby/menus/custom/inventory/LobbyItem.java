package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.modules.xg7menus.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LobbyItem {

    private final Item item;
    private final String conditionLine;
    private final String otherItemPath;

}
