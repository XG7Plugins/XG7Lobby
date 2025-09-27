package com.xg7plugins.xg7lobby.menus.custom.inventory.typeAdapter;

import com.xg7plugins.config.file.ConfigSection;
import org.bukkit.enchantments.Enchantment;

import java.util.function.Function;

public enum MaterialSettings {

    ENCHANTED_BOOK((config) -> {
        String enchant = config.get("enchant");
        int level = config.get("level", 1);

        return ":" + enchant.toUpperCase() + ":" + level;
    }),
    POTION((config) -> {

        String effect = config.get("effect", "WATER");
        boolean isStrong = config.get("is-strong", false);
        boolean isExtended = config.get("is-extended", false);

        return ":" + effect.toUpperCase() + ":" + isStrong + ":" + isExtended;

    });

    private final Function<ConfigSection, String> materialResultStringFunction;

    MaterialSettings(Function<ConfigSection, String> func) {
        this.materialResultStringFunction = func;
    }

    public String getSettings(ConfigSection config) {
        return materialResultStringFunction.apply(config);
    }

}
