package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.cache.ObjectCache;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;

import com.xg7plugins.xg7lobby.pvp.DeathCause;
import com.xg7plugins.xg7lobby.pvp.event.PlayerLeavePVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class CombatLogHandler implements Listener {

    private final ObjectCache<UUID, UUID> log;

    private final ConfigSection config = ConfigFile.of("pvp", XG7Lobby.getInstance()).root();

    public CombatLogHandler() {

        log = new ObjectCache<>(
                XG7Lobby.getInstance(),
                config.getTimeInMilliseconds("combat-log-remove"),
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
        return ConfigFile.of("pvp", XG7Lobby.getInstance()).root().get("enabled", false);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = getDamager(event.getDamager());

        if (damager == null) return;

        if (!XG7LobbyAPI.isPlayerInPVP(victim) || !XG7LobbyAPI.isPlayerInPVP(damager)) return;

        if (log.containsKey(victim.getUniqueId()).join() &&
                log.get(victim.getUniqueId()).join().equals(damager.getUniqueId())
                || log.containsKey(damager.getUniqueId()).join()
                && log.get(damager.getUniqueId()).join().equals(victim.getUniqueId())) return;



        log.put(victim.getUniqueId(), damager.getUniqueId());
        log.put(damager.getUniqueId(), victim.getUniqueId());

        Text.sendTextFromLang(damager, XG7Lobby.getInstance(), "pvp.tagged", Pair.of("target", victim.getName()), Pair.of("time", config.getTimeInMilliseconds("combat-log-remove") + ""));
        Text.sendTextFromLang(victim, XG7Lobby.getInstance(), "pvp.tagged", Pair.of("target", damager.getName()), Pair.of("time", config.getTimeInMilliseconds("combat-log-remove") + ""));
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

        XG7LobbyAPI.globalPVPManager().getHandler(KillPVPHandler.class).handle(player, Bukkit.getPlayer(log.get(player.getUniqueId()).join()), player.getLastDamageCause() != null ? DeathCause.fromCause(player.getLastDamageCause().getCause()) : DeathCause.GENERIC);
    }

    public void removeFromLog(Player player) {
        log.remove(player.getUniqueId());
    }
    public UUID getDamagerOf(Player player) {
        return log.get(player.getUniqueId()).join();
    }


}
