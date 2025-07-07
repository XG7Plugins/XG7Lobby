package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.pvp.DeathCause;
import com.xg7plugins.xg7lobby.pvp.GlobalPVPManager;
import com.xg7plugins.xg7lobby.pvp.event.PlayerKillInPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collections;
import java.util.List;

public class KillPVPHandler implements PVPHandler, Listener {

    private final PVPConfigs.OnKill config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnKill.class);
    private final PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);


    @Override
    public boolean isEnabled() {
        return pvpConfigs.isEnabled();
    }

    @Override
    public void handle(Player victim, Object... args) {

        Player killer = (Player) args[0];
        DeathCause deathCause = (DeathCause) args[1];

        ActionsProcessor.process(config.getVictimActions(), killer, Pair.of("victim", victim.getName()), Pair.of("cause", deathCause.name().toLowerCase()));

        if (killer != null) {
            ActionsProcessor.process(config.getKillerActions(), killer, Pair.of("victim", victim.getName()), Pair.of("killer", killer.getName()), Pair.of("cause", deathCause.name().toLowerCase()));
            XG7LobbyAPI.requestLobbyPlayer(killer.getUniqueId()).thenAccept(lobbyPlayer -> {
                lobbyPlayer.setGlobalPVPKills(lobbyPlayer.getGlobalPVPKills() + 1);
                lobbyPlayer.setGlobalPVPKillStreak(lobbyPlayer.getGlobalPVPKillStreak() + 1);
                XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
            });
        }
        XG7LobbyAPI.requestLobbyPlayer(victim.getUniqueId()).thenAccept(lobbyPlayer -> {
            lobbyPlayer.setGlobalPVPKills(lobbyPlayer.getGlobalPVPDeaths() + 1);
            lobbyPlayer.setGlobalPVPKillStreak(0);
            XG7LobbyAPI.lobbyPlayerManager().updatePlayer(lobbyPlayer);
        });

        List<Player> playersInPVP = XG7LobbyAPI.globalPVPManager().getAllPlayersInPVP();

        playersInPVP.forEach(player -> {
            if (killer != null) {
                Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-death-with-killer", Pair.of("victim", victim.getName()), Pair.of("killer", killer.getName()), Pair.of("cause", deathCause.name().toLowerCase()));
                return;
            }

            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-death-with-killer", Pair.of("victim", victim.getName()), Pair.of("cause", deathCause.name().toLowerCase()));
        });

        Bukkit.getPluginManager().callEvent(new PlayerKillInPVPEvent(killer, victim, deathCause));

    }

    @EventHandler
    public void onKillInPVP(PlayerDeathEvent event) {

        GlobalPVPManager globalPVPManager = XG7LobbyAPI.globalPVPManager();

        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity();

        DeathCause deathCause = DeathCause.fromCause(victim.getLastDamageCause().getCause());

        if (!globalPVPManager.isInPVP(victim)) return;
        if (killer != null && !globalPVPManager.isInPVP(killer)) return;

        handle(victim, killer, deathCause);

    }
}
