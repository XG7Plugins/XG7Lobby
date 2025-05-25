package com.xg7plugins.xg7lobby.aciton;

import com.cryptomorin.xseries.XPotion;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.function.BiConsumer;

public enum ActionType {

    MESSAGE(
            "[MESSAGE] message...",
            "Sends a message to a player",
            "WRITABLE_BOOK",
            false,
            (player, args) -> Text.detectLangsAndSend(player, XG7Lobby.getInstance(), args[0])
    ),
    PLAYER_COMMAND(
            "[PLAYER_COMMAND] command...",
            "Makes a player execute a command",
            "COMMAND_BLOCK",
            false,
            (player, args) -> player.performCommand(Text.detectLangs(player, XG7Lobby.getInstance(), args[0]).join().getPlainText())
    ),
    CONSOLE_COMMAND(
            "[CONSOLE_COMMAND] command...",
            "Executes a command from console",
            "COMMAND_BLOCK",
            false,
            (player, args) -> player.getServer().dispatchCommand(player.getServer().getConsoleSender(), Text.detectLangs(player, XG7Lobby.getInstance(), args[0]).join().getPlainText())
    ),
    TITLE(
            "[TITLE] title...",
            "Displays a title to a player",
            "NAME_TAG",
            true,
            (player, args) -> {
                if (args.length == 1) {
                    player.sendTitle(Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getPlainText(), "");
                    return;
                }
                if (args.length == 2) {
                    player.sendTitle(Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getPlainText(), Text.detectLangs(player,XG7Lobby.getInstance(),args[1]).join().getPlainText());
                    return;
                }
                if (args.length == 5) {
                    player.sendTitle(args[0].equals("_") ? "" : Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getPlainText(), args[1].equals("_") ? "" : Text.detectLangs(player,XG7Lobby.getInstance(),args[1]).join().getPlainText(), Parser.INTEGER.convert(args[2]), Parser.INTEGER.convert(args[3]), Parser.INTEGER.convert(args[4]));
                    return;
                }

                throw new ActionException("TITLE", "Incorrectly amount of args: " + args.length + ". The right way to use is \"[TITLE] title, Optional:[subtitle, Optional:[<fade, fade in, fade out>]].\"" +
                        "Use \"_\" to remove the title in the second case or subtitle or title in the last case.");

            }
    ),
    EFFECT(
            "[EFFECT] potion, duration, amplifier, Optional:[ambient, Optional:[particles, Optional:[icon]]].",
            "Gives a effect to a player",
            "POTION",
            true,
            (player, args) -> {
                try {
                    switch (args.length) {
                        case 3:
                            player.addPotionEffect(new PotionEffect(XPotion.valueOf(args[0]).getPotionEffectType(), Parser.INTEGER.convert(args[1]), Parser.INTEGER.convert(args[2])));
                            break;
                        case 4:
                            player.addPotionEffect(new PotionEffect(XPotion.valueOf(args[0]).getPotionEffectType(), Parser.INTEGER.convert(args[1]), Parser.INTEGER.convert(args[2]), Parser.BOOLEAN.convert(args[3])));
                            break;
                        case 5:
                            player.addPotionEffect(new PotionEffect(XPotion.valueOf(args[0]).getPotionEffectType(), Parser.INTEGER.convert(args[1]), Parser.INTEGER.convert(args[2]), Parser.BOOLEAN.convert(args[3]), Parser.BOOLEAN.convert(args[4])));
                            break;
                        default:
                            throw new ActionException("EFFECT", "Incorrectly amount of args: " + args.length + ". The right way to use is [EFFECT] potion, duration, amplifier, Optional:[ambient, Optional:[particles, Optional:[icon]]].");

                    }
                } catch (Throwable e) {
                    throw new ActionException("EFFECT", "Unable to convert text in values, check if the values are correct. potion: TEXT (ENUM_NAME), duration: INTEGER, amplifier: INTEGER, ambient: BOOLEAN, particles: BOOLEAN, icon: BOOLEAN");
                }
            }
    ),
    TP(
            "[TP] world, x, y, z, Optional:[yaw,pitch]",
            "Teleports a player to a specific location",
            "ENDER_PEARL",
            true,
            (player, args) -> {
                try {
                    switch (args.length) {
                        case 4:
                            Location location = Location.of(args[0], Parser.DOUBLE.convert(args[1]), Parser.DOUBLE.convert(args[2]), Parser.DOUBLE.convert(args[3]));
                            location.teleport(player);
                            break;
                        case 6:
                            Location location2 = Location.of(args[0], Parser.DOUBLE.convert(args[1]), Parser.DOUBLE.convert(args[2]), Parser.DOUBLE.convert(args[3]), Parser.FLOAT.convert(args[4]), Parser.FLOAT.convert(args[5]));
                            location2.teleport(player);
                            break;
                        default:
                            throw new ActionException("TP", "Incorrectly amount of args: " + args.length + ". The right way to use is [TP] world, x, y, z, Optional:[yaw,pitch].");
                    }
                } catch (Throwable e) {
                    throw new ActionException("TP", "Unable to convert text in values, check if the values are correct. world: TEXT: (WORLD NAME), x: DECIMAL, y: DECIMAL, z: DECIMAL, yaw: DECIMAL, pitch: DECIMAL");
                }
            }
    )
    ;

    private final String usage;
    private final String description;
    private final String icon;
    private final boolean needArgs;
    private final BiConsumer<Player, String[]> action;

    ActionType(String usage, String description, String icon, boolean needArgs, BiConsumer<Player, String[]> action) {
        this.usage = usage;
        this.description = description;
        this.icon = icon;
        this.needArgs = needArgs;
        this.action = action;
    }

    public void execute(Player player, String[] args) {
        action.accept(player, args);
    }

    public static ActionType extractType(String s) {
        s = s.replace("[", "").replace("]", "");
        for (ActionType type : values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }

    private enum Slot {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        OFFHAND
    }

}
