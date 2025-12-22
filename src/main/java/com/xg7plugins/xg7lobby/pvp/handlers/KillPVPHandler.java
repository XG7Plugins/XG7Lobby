package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.pvp.DeathCause;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import com.xg7plugins.xg7lobby.pvp.event.PlayerKillInPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;

public class KillPVPHandler implements PVPHandler {

    private final ConfigSection pvpConfigs = ConfigFile.of("pvp", XG7LobbyLoader.getInstance()).root();
    private final ConfigSection config = ConfigFile.of("pvp", XG7LobbyLoader.getInstance()).section("on-kill");


    @Override
    public boolean isEnabled() {
        return pvpConfigs.get("enabled", false);
    }

    @Override
    public void handle(Player victim, Object... args) {

        Player killer = (Player) args[0];
        DeathCause deathCause = (DeathCause) args[1];

        ActionsProcessor.process(config.get("victim-actions"), victim, Pair.of("victim", victim.getName()), Pair.of("cause", deathCause.name().toLowerCase()));

        if (killer != null) {
            ActionsProcessor.process(config.get("killer-actions"), killer, Pair.of("victim", victim.getName()), Pair.of("killer", killer.getName()), Pair.of("cause", deathCause.name().toLowerCase()));
            XG7LobbyAPI.requestLobbyPlayer(killer.getUniqueId()).thenAccept(lobbyPlayer -> {
                lobbyPlayer.setGlobalPVPKills(lobbyPlayer.getGlobalPVPKills() + 1);
                lobbyPlayer.setGlobalPVPKillStreak(lobbyPlayer.getGlobalPVPKillStreak() + 1);
                XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
            });
        }
        XG7LobbyAPI.requestLobbyPlayer(victim.getUniqueId()).thenAccept(lobbyPlayer -> {
            lobbyPlayer.setGlobalPVPDeaths(lobbyPlayer.getGlobalPVPDeaths() + 1);
            lobbyPlayer.setGlobalPVPKillStreak(0);
            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
        });

        List<Player> playersInPVP = XG7LobbyAPI.globalPVPManager().getAllPlayersInPVP();

        if (killer != null) XG7LobbyAPI.globalPVPManager().getCombatLogHandler().removeFromLog(killer);
        XG7LobbyAPI.globalPVPManager().getCombatLogHandler().removeFromLog(victim);

        playersInPVP.forEach(player -> {
            if (killer != null) {
                Text.sendTextFromLang(player, XG7LobbyLoader.getInstance(), "pvp.on-death-with-killer", Pair.of("victim", victim.getName()), Pair.of("killer", killer.getName()), Pair.of("cause", Text.fromLang(player, XG7LobbyLoader.getInstance(), deathCause.getLangPath()).join().getText()));
                return;
            }

            Text.sendTextFromLang(player, XG7LobbyLoader.getInstance(), "pvp.on-death", Pair.of("victim", victim.getName()), Pair.of("cause", Text.fromLang(player, XG7LobbyLoader.getInstance(), deathCause.getLangPath()).join().getText()));
        });

        Bukkit.getPluginManager().callEvent(new PlayerKillInPVPEvent(killer, victim, deathCause));

    }

    @EventHandler
    public void onKillInPVP(PlayerDeathEvent event) {

        GlobalPVPManager globalPVPManager = XG7LobbyAPI.globalPVPManager();

        Player victim = event.getEntity();

        if (!XG7LobbyAPI.globalPVPManager().isInPVP(victim)) return;

        UUID killerUUID = globalPVPManager.getCombatLogHandler().getDamagerOf(victim);

        event.setDeathMessage(null);

        if (killerUUID == null) {
            handle(victim, null, victim.getLastDamageCause() != null ? DeathCause.fromCause(victim.getLastDamageCause().getCause()) : DeathCause.GENERIC);
            return;
        }
        Player killer = Bukkit.getPlayer(killerUUID);

        DeathCause deathCause = DeathCause.fromCause(victim.getLastDamageCause().getCause());

        if (killer != null && !globalPVPManager.isInPVP(killer)) return;


        handle(victim, killer, deathCause);

    }
}
