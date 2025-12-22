package com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7menus.menus.interfaces.gui.MenuConfigurations;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;

public class InfractionsMenuConfiguration implements MenuConfigurations {

    @Setter
    private OfflinePlayer target;

    @Override
    public String getTitle() {
        return "lang:[warn-menu.title]";
    }

    @Override
    public String getId() {
        return "warns-menu";
    }

    @Override
    public Plugin getPlugin() {
        return XG7LobbyLoader.getInstance();
    }

    @Override
    public List<Pair<String,String>> getPlaceholders() {
        return Arrays.asList(Pair.of("target", target.getName()));
    }

    @Override
    public int getRows() {
        return 6;
    }
}
