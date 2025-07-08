package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.cache.ObjectCache;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.DeathCause;
import com.xg7plugins.xg7lobby.pvp.event.PlayerLeavePVPEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

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

        //1 -> victim, 2 -> damager
    }


    @Override
    public boolean isEnabled() {
        return Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isEnabled();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        Player victim = (Player) event.getEntity();
        Player damager = getDamager(event.getDamager());

        if (damager == null) return;

        if (!XG7LobbyAPI.isPlayerInPVP(victim) && !XG7LobbyAPI.isPlayerInPVP(damager)) return;

        if (log.containsKey(victim.getUniqueId()).join() &&
                log.get(victim.getUniqueId()).join().equals(damager.getUniqueId())
                || log.containsKey(damager.getUniqueId()).join()
                && log.get(damager.getUniqueId()).join().equals(victim.getUniqueId())) return;



        log.put(victim.getUniqueId(), damager.getUniqueId());
        log.put(damager.getUniqueId(), victim.getUniqueId());

        Text.sendTextFromLang(damager, XG7Lobby.getInstance(), "pvp.tagged", Pair.of("target", victim.getName()), Pair.of("time", pvpConfigs.getCombatLogRemove().getMilliseconds() + ""));
        Text.sendTextFromLang(victim, XG7Lobby.getInstance(), "pvp.tagged", Pair.of("target", damager.getName()), Pair.of("time", pvpConfigs.getCombatLogRemove().getMilliseconds() + ""));
    }

    private Player getDamager(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) return (Player) projectile.getShooter();
        }
        return null;
    }

    @EventHandler
    public void onPVPLeave(PlayerLeavePVPEvent event) {
        Player player = event.getPlayer();

        if (!log.containsKey(player.getUniqueId()).join()) return;

        XG7LobbyAPI.globalPVPManager().getHandler(KillPVPHandler.class).handle(player, player.getKiller(), player.getLastDamageCause() != null ? DeathCause.fromCause(player.getLastDamageCause().getCause()) : DeathCause.GENERIC);
    }

    public void removeFromLog(Player player) {
        log.remove(player.getUniqueId());
    }
    public UUID getDamagerOf(Player player) {
        return log.get(player.getUniqueId()).join();
    }


}
