package com.xg7plugins.xg7lobby.events.lobby_events;

import com.xg7plugins.data.config.ConfigBoolean;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
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
            isEnabled = @ConfigBoolean(configName = "config", path = "spawn-mobs")
    )
    public void onMobSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(configName = "config", path = "leaves-decay")
    )
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(configName = "config", path = "burn-blocks")
    )
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(configName = "config", path = "block-spread")
    )
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(configName = "config", path = "cancel-explosions", invert = true)
    )
    public void onExplosion(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

}
