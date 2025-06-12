package com.xg7plugins.xg7lobby.menus.custom.inventory;

import com.xg7plugins.modules.xg7menus.menus.BasicMenu;

import java.util.HashMap;

public interface LobbyInventory {

    HashMap<String, LobbyItem> items();
    HashMap<Integer, String> grid();

    BasicMenu getMenu();

}
