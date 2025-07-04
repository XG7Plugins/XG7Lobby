package com.xg7plugins.xg7lobby.data.player;

import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());


        if (!XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), player)) return;

        player.setAllowFlight(flying || (
                                (config.get("multi-jumps.enabled", Boolean.class).orElse(false) && player.hasPermission("xg7lobby.multi-jumps"))
                                || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR
                )
        );
        if (player.getAllowFlight()) player.setFlying(flying);

    }

    public void applyBuild() {
        Player player = this.getPlayer();

        if (player == null) return;

        if (buildEnabled) {
            XG7LobbyAPI.customInventoryManager().closeAllMenus(player);
            return;
        }

        Config mainConfig = Config.mainConfigOf(XG7Lobby.getInstance());

        XG7LobbyAPI.customInventoryManager().openMenu(mainConfig.get("main-selector-id", String.class).orElse("selector"), player);

    }

    public void applyHide() {
        Player player = this.getPlayer();
        if (player == null) return;

        List<Runnable> tasks = new ArrayList<>();

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p))
                .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                .forEach(p -> {
                    LobbyPlayer otherPlayer = XG7LobbyAPI.getLobbyPlayer(p.getUniqueId());

                    tasks.add(() -> {
                        if (hidingPlayers) player.hidePlayer(p);
                        else player.showPlayer(p);

                        if (otherPlayer.isHidingPlayers()) p.hidePlayer(player);
                        else p.showPlayer(player);
                    });
                });

        XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> tasks.forEach(Runnable::run)));
    }

    public boolean isBuildEnabled() {
        return buildEnabled && Config.mainConfigOf(XG7Lobby.getInstance()).get("build-system-enabled", Boolean.class).orElse(false);
    }

    public void addInfraction(Infraction infraction) {
        this.infractions.add(infraction);
    }

    public void applyInfractions() {

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        OfflinePlayer player = getOfflinePlayer();
        
        Infraction newInfraction = infractions.get(infractions.size() - 1);

        LobbyPlayerManager playerManager = XG7LobbyAPI.lobbyPlayerManager();

        boolean banIp = config.get("infraction-ban-by-ip", Boolean.class).orElse(false);

        config.getList("infraction-levels", Map.class).ifPresent(levels -> {
            long infractionCount = infractions.stream().filter(infraction -> infraction.getLevel() == newInfraction.getLevel()).count();
            levels.stream().filter(map -> map.get("level").equals(newInfraction.getLevel())).findFirst().ifPresent(map -> {

                int infractionsToBan = (int) map.get("min-to-ban");
                int infractionsToKick = (int) map.get("min-to-kick");
                int infractionsToMute = (int) map.get("min-to-mute");

                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> {
                    if ((infractionCount >= infractionsToBan && infractionsToBan > -1)) {
                        if (player.isOnline()) playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));

                        if (banIp && player.isOnline()) {
                            playerManager.banIpPlayer(player.getPlayer(), null, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                        }
                        else {
                            playerManager.banPlayer(player, null, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                        }
                    }

                    if (((infractionCount >= infractionsToKick && infractionsToKick > -1)) && player.isOnline()) {
                        playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-kick").join().replace("reason", newInfraction.getWarning()));
                    }
                }));
                if ((infractionCount >= infractionsToMute && infractionsToMute > -1)) {
                    Time unmuteTime = config.get("infraction-time-to-unmute",String.class).orElse("").toLowerCase().equals("forever") ? Time.of(0) : config.getTime("infraction-time-to-unmute").orElse(Time.of(30, 0)).add(Time.now());

                    playerManager.mutePlayer(this, unmuteTime, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-mute").join().replace("reason", newInfraction.getWarning()));
                }
            });

            int totalInfractionCount = infractions.size();

            int totalInfractionsToBan = config.get("total-infractions-to-ban", Integer.class).orElse(-1);
            int totalInfractionsToKick = config.get("total-infractions-to-kick", Integer.class).orElse(-1);
            int totalInfractionsToMute = config.get("total-infractions-to-mute", Integer.class).orElse(-1);

            if (totalInfractionCount >= totalInfractionsToMute) {
                Time unmuteTime = config.get("infraction-time-to-unmute",String.class).orElse("").toLowerCase().equals("forever") ? Time.of(0) : config.getTime("infraction-time-to-unmute").orElse(Time.of(30, 0)).add(Time.now());

                playerManager.mutePlayer(this, unmuteTime, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-mute").join().replace("reason", newInfraction.getWarning()));
            }

            if (totalInfractionCount >= totalInfractionsToBan) {
                if (player.isOnline()) playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));

                if (banIp && player.isOnline()) {
                    playerManager.banIpPlayer(player.getPlayer(), null, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                }
                else {
                    playerManager.banPlayer(player, null, Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-ban").join().replace("reason", newInfraction.getWarning()));
                }
            }

            if (totalInfractionCount >= totalInfractionsToKick && player.isOnline()) {
                playerManager.kickPlayer(player.getPlayer(), Text.fromLang(getPlayer(),XG7Lobby.getInstance(), "commands.infraction.on-kick").join().replace("reason", newInfraction.getWarning()));
            }

        });
        
    }



}
