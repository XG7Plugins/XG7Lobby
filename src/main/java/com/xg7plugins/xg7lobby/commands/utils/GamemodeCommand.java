package com.xg7plugins.xg7lobby.commands.utils;

import com.xg7plugins.libs.xseries.XMaterial;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandSetup(
        name = "gamemode",
        permission = "xg7lobby.command.gamemode",
        syntax = "/7lgamemode <gamemode> (player)",
        description = "Change player's gamemode",
        pluginClass = XG7LobbyLoader.class
)
public class GamemodeCommand implements Command {

    @Override
    public Plugin getPlugin() {
        return XG7LobbyLoader.getInstance();
    }

    @Override
    public CommandState onCommand(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.syntaxError(getCommandSetup().syntax());
        }

        OfflinePlayer target = null;

        if (args.len() == 1) {
            if (!(sender instanceof Player)) {
                return CommandState.NOT_A_PLAYER;
            }
            target = (Player) sender;
        }

        boolean isOther = false;

        if (args.len() > 1) {
            if (!sender.hasPermission("xg7lobby.command.game-mode-other")) {
                return CommandState.NO_PERMISSION;
            }
            target = args.get(1, OfflinePlayer.class);
            isOther = true;
        }

        Mode mode = Mode.getMode(args.get(0, String.class));

        if (mode == null) {
            Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "commands.game-mode.invalid-game-mode");
            return CommandState.ERROR;
        }

        if (isOther) {

            if (target == null || (!target.hasPlayedBefore()) && !target.isOnline()) {
                return CommandState.PLAYER_NOT_FOUND;
            }

            if (!target.isOnline()) {
                return CommandState.NOT_ONLINE;
            }
        }

        target.getPlayer().setGameMode(mode.getGameMode());

        if (mode == Mode.SURVIVAL || mode == Mode.ADVENTURE) {
            XG7LobbyAPI.requestLobbyPlayer(target.getUniqueId()).thenAccept(LobbyPlayer::fly);
        }

        OfflinePlayer finalTarget = target;
        Text.sendTextFromLang(target.getPlayer(), XG7LobbyLoader.getInstance(), "commands.game-mode.set", Pair.of("gamemode", mode.name().toLowerCase()));
        if (isOther) Text.sendTextFromLang(sender, XG7LobbyLoader.getInstance(), "commands.game-mode.set-other", Pair.of("gamemode", mode.name().toLowerCase()));

        return CommandState.FINE;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {
        switch (args.len()) {
            case 1:
                return Arrays.asList("s", "c", "a", "sp");
            case 2:
                if (!sender.hasPermission("xg7lobby.command.gamemode-other")) return Collections.emptyList();
                return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
        }

        return Collections.emptyList();
    }

    @Override
    public Item getIcon() {
        return Item.commandIcon(XMaterial.GRASS_BLOCK, this);
    }

    enum Mode {
        SURVIVAL("s", "0"),
        CREATIVE("c", "1"),
        ADVENTURE("a", "2"),
        SPECTATOR("sp", "3");

        private final String[] aliases;

        Mode(String... aliases) {
            this.aliases = aliases;
        }

        public static Mode getMode(String name) {
            for (Mode mode : values()) {
                if (mode.name().equalsIgnoreCase(name)) return mode;
                for (String alias : mode.aliases) {
                    if (alias.equalsIgnoreCase(name)) return mode;
                }
            }
            return null;
        }

        public GameMode getGameMode() {
            switch (this) {
                case SURVIVAL:
                    return GameMode.SURVIVAL;
                case CREATIVE:
                    return GameMode.CREATIVE;
                case ADVENTURE:
                    return GameMode.ADVENTURE;
                case SPECTATOR:
                    return GameMode.SPECTATOR;
                default:
                    return null;
            }
        }
    }
}