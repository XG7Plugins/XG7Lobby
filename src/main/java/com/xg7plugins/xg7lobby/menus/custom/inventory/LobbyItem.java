package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LobbyItem {

    private final Item item;
    private final Pair<Condition, String> condition;
    private final String otherItemPath;

}
