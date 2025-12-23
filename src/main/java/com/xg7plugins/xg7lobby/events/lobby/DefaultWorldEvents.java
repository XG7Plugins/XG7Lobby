package com.xg7plugins.xg7lobby.events.lobby;


import com.xg7plugins.config.
utils.ConfigCheck;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class DefaultWorldEvents implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }
    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigCheck(configName = "config", path = "spawn-mobs", invert = true)
    )
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Projectile) return;
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigCheck(configName = "config", path = "leaves-decay", invert = true)
    )
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigCheck(configName = "config", path = "burn-blocks", invert = true)
    )
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigCheck(configName = "config", path = "block-spread", invert = true)
    )
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigCheck(configName = "config", path = "cancel-explosions")
    )
    public void onExplosion(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

}
