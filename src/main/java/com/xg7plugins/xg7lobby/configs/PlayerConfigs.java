package com.xg7plugins.xg7lobby.configs;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.xg7lobby.XG7Lobby;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Getter
public class PlayerConfigs {

    public final double hearts;
    public final int hunger;
    public final boolean foodChange;

    public final boolean interactWithBlocks;
    public final boolean breakBlock;
    public final boolean placeBlock;

    public final boolean dropItems;
    public final boolean pickupItems;

    public final boolean takeDamage;
    public final boolean attack;

    public final boolean cancelDeathByVoid;
    public final boolean cancelPortalTeleport;

    public PlayerConfigs() {

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        this.hearts = config.get("hearts", Double.class).orElse(1D);
        this.hunger = config.get("hunger", Integer.class).orElse(10);
        this.foodChange = config.get("food-change", Boolean.class).orElse(false);

        this.interactWithBlocks = config.get("interact-with-blocks", Boolean.class).orElse(false);
        this.breakBlock = config.get("break-block", Boolean.class).orElse(false);
        this.placeBlock = config.get("place-block", Boolean.class).orElse(false);

        this.dropItems = config.get("drop-items", Boolean.class).orElse(false);
        this.pickupItems = config.get("pickup-items", Boolean.class).orElse(false);

        this.takeDamage = config.get("take-damage", Boolean.class).orElse(false);
        this.attack = config.get("attack", Boolean.class).orElse(false);

        this.cancelDeathByVoid = config.get("cancel-death-by-void", Boolean.class).orElse(true);
        this.cancelPortalTeleport = config.get("cancel-portal-teleport", Boolean.class).orElse(true);
    }

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
    }

}
