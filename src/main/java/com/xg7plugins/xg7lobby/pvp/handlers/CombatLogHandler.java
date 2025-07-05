package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.DeathCause;
import com.xg7plugins.xg7lobby.pvp.event.PlayerLeavePVPEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class CombatLogHandler implements Listener {

    private final ObjectCache<UUID, UUID> log;

    public CombatLogHandler() {
        PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        log = new ObjectCache<>(
                XG7Lobby.getInstance(),
                pvpConfigs.getCombatLogRemove().getMilliseconds(),
                true,
                "lobby-combat-log",
                true,
                UUID.class,
                UUID.class
        );

        //1 -> victim, 2 -> killer
    }


    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isEnabled();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!XG7LobbyAPI.isPlayerInPVP(victim) && !XG7LobbyAPI.isPlayerInPVP(damager)) return;

        log.put(victim.getUniqueId(), damager.getUniqueId());
    }

    @EventHandler
    public void onPVPLeave(PlayerLeavePVPEvent event) {
        Player player = event.getPlayer();

        if (!log.containsKey(player.getUniqueId()).join()) return;

        XG7LobbyAPI.globalPVPManager().getHandler(KillPVPHandler.class).handle(player, player.getKiller(), DeathCause.fromCause(player.getLastDamageCause().getCause()));
    }


}
