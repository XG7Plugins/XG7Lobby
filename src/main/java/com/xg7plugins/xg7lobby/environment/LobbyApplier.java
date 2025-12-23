package com.xg7plugins.xg7lobby.environment;

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
        player.setAllowFlight(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR));
        player.setFlying(false);
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        Bukkit.getOnlinePlayers().forEach(player::showPlayer);
    }

}
