package com.xg7plugins.xg7lobby.events;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class LobbyApplier {

    public static void apply(Player player) {

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root();

        player.setMaxHealth(config.get("hearts", 10) * 2);
        player.setFoodLevel(config.get("hunger", 10) * 2);

    }

    public static void reset(Player player) {
        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("on-world-leave");

        if (config.get("reset-flight")) {
            player.setAllowFlight(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR));
            player.setFlying(config.get("flying", false));
        }

        if (config.get("reset-hearts")) {
            player.setMaxHealth(config.get("hearts", 10) * 2);
            if (config.get("heal")) player.setHealth(player.getMaxHealth());
        }

        if (config.get("reset-food")) {
            player.setFoodLevel(config.get("food", 10) * 2);
        }

        if (config.get("remove-potion-effects")) {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }

        Bukkit.getOnlinePlayers().forEach(player::showPlayer);
    }

}
