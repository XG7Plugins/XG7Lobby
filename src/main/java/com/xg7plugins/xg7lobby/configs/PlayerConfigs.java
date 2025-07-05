package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigFile;
import com.xg7plugins.data.config.section.ConfigSection;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Getter
@ConfigFile(plugin = XG7Lobby.class, configName = "config")
public class PlayerConfigs extends ConfigSection {

    private double hearts;
    private int hunger;
    private boolean foodChange;

    private boolean interactWithBlocks;
    private boolean breakBlock;
    private boolean placeBlock;

    private boolean dropItems;
    private boolean pickupItems;

    private boolean takeDamage;
    private boolean attack;

    private boolean cancelDeathByVoid;
    private boolean cancelPortalTeleport;

    public void apply(Player player) {

        player.setMaxHealth(hearts * 2);
        player.setFoodLevel(hunger * 2);

    }

    public void reset(Player player) {
        player.setAllowFlight(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR));
        player.setFlying(false);
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        Bukkit.getOnlinePlayers().forEach(player::showPlayer);
    }

}
