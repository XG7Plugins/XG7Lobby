package com.xg7plugins.xg7lobby.data.player;

import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@Data
@Table(name = "lobby_players")
public class LobbyPlayer implements Entity<UUID, LobbyPlayer> {

    @Pkey
    private final UUID playerUUID;
    private boolean muted;

    @Column(name = "unmute_time")
    private Time unmuteTime;

    @Column(name = "hiding_players")
    private boolean hidingPlayers;
    @Column(name = "build_enabled")
    private boolean buildEnabled;
    @Column(name = "fly_enabled")
    private boolean flying;

    @Column(name = "global_pvp_kills")
    private int globalPVPKills;
    @Column(name = "global_pvp_deaths")
    private int globalPVPDeaths;
    @Column(name = "global_pvp_kill_streak")
    private int globalPVPKillStreak;
    private final List<Infraction> infractions = new ArrayList<>();

    public LobbyPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
        unmuteTime = Time.of(0);
    }

    private LobbyPlayer() {
        playerUUID = null;
    }


    @Override
    public boolean equals(LobbyPlayer other) {
        return other.getID().equals(this.getID());
    }

    @Override
    public UUID getID() {
        return playerUUID;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getServer().getOfflinePlayer(playerUUID);
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(playerUUID);
    }

    public void fly() {
        Player player = this.getPlayer();

        if (player == null) return;

        if (!XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

        player.setAllowFlight(flying || (
                        (
                                ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("multi-jumps").get("enabled", true)
                                && player.hasPermission("xg7lobby.multi-jumps"))
                                || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR
                )
        );
        if (player.getAllowFlight()) player.setFlying(flying);

    }

    public void applyBuild() {
        Player player = this.getPlayer();

        if (player == null) return;

        if (buildEnabled) {
            if (XG7LobbyAPI.customInventoryManager() != null) XG7LobbyAPI.customInventoryManager().closeAllMenus(player);
            return;
        }

        XG7LobbyAPI.customInventoryManager().openMenu(ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("main-selector-id"), player);

    }

    public void applyHide() {
        Player player = this.getPlayer();
        if (player == null) return;

        if (XG7LobbyAPI.globalPVPManager().isInPVP(player)) return;

        List<Runnable> tasks = new ArrayList<>();

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p))
                .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                .forEach(p -> {
                    LobbyPlayer otherPlayer = XG7LobbyAPI.getLobbyPlayer(p.getUniqueId());

                    tasks.add(() -> {

                        if (hidingPlayers) player.hidePlayer(p);
                        else player.showPlayer(p);

                        if (XG7LobbyAPI.globalPVPManager().isInPVP(p)) return;

                        if (otherPlayer.isHidingPlayers()) p.hidePlayer(player);
                        else p.showPlayer(player);
                    });
                });

        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> tasks.forEach(Runnable::run)));
    }

    public boolean isBuildEnabled() {
        return buildEnabled && ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root().get("build-system-enabled", true);
    }

    public void addInfraction(Infraction infraction) {
        this.infractions.add(infraction);
    }

    public void applyInfractions() {

        OfflinePlayer player = getOfflinePlayer();

        if (infractions.isEmpty()) return;

        Infraction newInfraction = infractions.get(infractions.size() - 1);

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        ConfigSection config = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).root();

        long infractionCount = infractions.stream().filter(infraction -> infraction.getLevel() == newInfraction.getLevel()).count();


        config.getList("infraction-levels", Map.class).orElse(Collections.emptyList()).stream().filter(map -> map.get("level").equals(newInfraction.getLevel())).findFirst().ifPresent(map -> {

            int infractionsToBan = (int) map.get("min-to-ban");
            int infractionsToKick = (int) map.get("min-to-kick");
            int infractionsToMute = (int) map.get("min-to-mute");

            XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
                if ((infractionCount >= infractionsToBan && infractionsToBan > -1)) {
                    if (player.isOnline())
                        playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));

                    if (config.get("infractions-ban-by-ip", false) && player.isOnline()) {
                        playerManager.banIpPlayer(player.getPlayer(), null, Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                    } else {
                        playerManager.banPlayer(player, null, Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                    }
                }

                if (((infractionCount >= infractionsToKick && infractionsToKick > -1)) && player.isOnline()) {
                    playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-kick").join().replace("reason", newInfraction.getWarning()));
                }
            }));
            if ((infractionCount >= infractionsToMute && infractionsToMute > -1)) {
                muteByInfraction(newInfraction, playerManager, config);
            }
        });

        int totalInfractionCount = infractions.size();

        if (totalInfractionCount >= config.get("total-infractions-to-mute", 2)) {
            muteByInfraction(newInfraction, playerManager, config);
        }

        if (totalInfractionCount >= config.get("total-infractions-to-ban", 10)) {
            if (player.isOnline())
                playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));

            if (config.get("infractions-ban-by-ip:", false) && player.isOnline()) {
                playerManager.banIpPlayer(player.getPlayer(), null, Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
            } else {
                playerManager.banPlayer(player, null, Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
            }
        }

        if (totalInfractionCount >= config.get("total-infractions-to-kick", 5) && player.isOnline()) {
            playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-kick").join().replace("reason", newInfraction.getWarning()));
        }

    }

    private void muteByInfraction(Infraction newInfraction, LobbyPlayerManager playerManager, ConfigSection config) {
        Time unmuteTime = config.get("infraction-time-to-unmute", "").toLowerCase().equals("forever") ? Time.of(0) : config.getTimeOrDefault("infraction-time-to-unmute", Time.of(30, 0)).add(Time.now());

        playerManager.mutePlayer(this, unmuteTime, Text.fromLang(getPlayer(), XG7Lobby.getInstance(), "commands.infraction.on-mute").join().replace("reason", newInfraction.getWarning()));
    }

}

