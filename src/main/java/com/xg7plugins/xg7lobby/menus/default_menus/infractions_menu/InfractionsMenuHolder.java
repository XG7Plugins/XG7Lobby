package com.xg7plugins.xg7lobby.menus.default_menus.infractions_menu;

import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class InfractionsMenuHolder extends MenuHolder {

    private final OfflinePlayer target;

    private int page = 0;

    public InfractionsMenuHolder(InfractionsMenu menu, OfflinePlayer target, Player player) {
        super(menu, player);
        this.target = target;
    }

    public void goPage(int page) {
        getMenu().goPage(page, this);
        this.page = page;
    }
    public void nextPage() {
        goPage(page + 1);
    }
    public void previousPage() {
        goPage(page - 1);
    }

    @Override
    public InfractionsMenu getMenu() {
        return (InfractionsMenu) super.getMenu();
    }
}
