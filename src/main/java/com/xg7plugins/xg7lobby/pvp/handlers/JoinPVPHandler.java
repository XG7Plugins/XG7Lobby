package com.xg7plugins.xg7lobby.pvp.handlers;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.events.LobbyListener;
import com.xg7plugins.xg7lobby.pvp.event.PlayerJoinPVPEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class JoinPVPHandler implements PVPHandler, LobbyListener {

    private final PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

    @Override
    public void handle(Player player, Object... args) {

        Text.sendTextFromLang(player, XG7Lobby.getInstance(), "pvp.on-join");

        PVPConfigs.OnJoinPvp config = Config.of(XG7Lobby.getInstance(), PVPConfigs.OnJoinPvp.class);
        PVPConfigs pvpConfigs = Config.of(XG7Lobby.getInstance(), PVPConfigs.class);

        player.setGameMode(GameMode.SURVIVAL);
        Config.of(XG7Lobby.getInstance(), PlayerConfigs.class).reset(player);

        XG7LobbyAPI.customInventoryManager().closeAllMenus(player);

        XG7LobbyAPI.customInventoryManager().openMenu(Config.mainConfigOf(XG7Lobby.getInstance()).get("main-pvp-selector-id", String.class).orElse("pvp"), player);

        if (config.isHeal()) player.setHealth(player.getMaxHealth());

        if (config.isTeleportToPvpLobby()) {
            XG7LobbyAPI.requestLobbyLocation(pvpConfigs.getPvpLobby()).thenAccept(l -> l.teleport(player));
        }

        Bukkit.getOnlinePlayers().stream().filter(p -> !XG7LobbyAPI.isPlayerInPVP(p)).forEach(p -> applyPVPHiding(player, p));
        ActionsProcessor.process(config.getActions(), player);

        Bukkit.getPluginManager().callEvent(new PlayerJoinPVPEvent(player));

    }

    @Override
    public boolean isEnabled() {
        return pvpConfigs.isEnabled();
    }

    @Override
    public void onWorldJoin(Player player, World newWorld) {
        Bukkit.getOnlinePlayers().stream().filter(XG7LobbyAPI::isPlayerInPVP).forEach(p -> applyPVPHiding(p, player));
    }

    private void applyPVPHiding(Player player, Player other) {
        if (!pvpConfigs.isHidePlayersNotInPvp()) return;
        if (XG7LobbyAPI.isPlayerInPVP(other)) return;
        player.hidePlayer(other);
    }
}
