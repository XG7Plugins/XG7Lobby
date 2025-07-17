package com.xg7plugins.xg7lobby.acitons;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.modules.xg7menus.XG7Menus;
import com.xg7plugins.modules.xg7menus.item.Item;
import com.xg7plugins.modules.xg7menus.menus.BasicMenu;
import com.xg7plugins.modules.xg7menus.menus.menuholders.BasicMenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.MenuHolder;
import com.xg7plugins.modules.xg7menus.menus.menuholders.PlayerMenuHolder;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.CustomInventoryManager;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyInventory;
import com.xg7plugins.xg7lobby.menus.custom.inventory.LobbyItem;
import com.xg7plugins.xg7lobby.menus.custom.inventory.gui.LobbyGUI;
import com.xg7plugins.xg7lobby.menus.custom.inventory.hotbar.LobbyHotbar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@Getter
@AllArgsConstructor
public enum ActionType {

    MESSAGE(
            "[MESSAGE] message...",
            "Sends a message to a player",
            "WRITABLE_BOOK",
            false,
            (player, args) -> Text.detectLangsAndSend(player, XG7Lobby.getInstance(), args[0]).join()
    ),
    BROADCAST(
            "[BROADCAST] message...",
            "Broadcasts a message to all players",
            "PAPER",
            false,
            (player, args) -> Bukkit.broadcastMessage(Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getText())
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
                    player.sendTitle(Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getText(), "");
                    return;
                }
                if (args.length == 2) {
                    player.sendTitle(Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getText(), Text.detectLangs(player,XG7Lobby.getInstance(),args[1]).join().getText());
                    return;
                }
                if (args.length == 5) {
                    player.sendTitle(args[0].equals("_") ? "" : Text.detectLangs(player,XG7Lobby.getInstance(),args[0]).join().getText(), args[1].equals("_") ? "" : Text.detectLangs(player,XG7Lobby.getInstance(),args[1]).join().getText(), Parser.INTEGER.convert(args[2]), Parser.INTEGER.convert(args[3]), Parser.INTEGER.convert(args[4]));
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
    ),
    SUMMON(
            "[SUMMON] entity",
            "Summons a entity",
            "EGG",
            true,
            (player, args) -> Location.fromPlayer(player).spawnEntity(XEntityType.valueOf(args[0].toUpperCase()).get())
    ),
    SOUND(
            "[SOUND] sound, Optional:[volume, Optional:[pitch]]",
            "Plays a sound to a player",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWVjYjQ5Y2NjYzEzNmIyZjQ3OTJhYTE5MDY3ZGM2NDVhNGVmYTEyYzM3NzQxM2QxOGNkMjEyNzM4YjE5NjlhYSJ9fX0",
            true,
            (player, args) -> {

                Location location = Location.fromPlayer(player);
                try {
                    switch (args.length) {
                        case 1:
                            location.playSound(XSound.of(args[0]).orElse(XSound.MUSIC_DISC_13).get(), 1, 1);
                            return;
                        case 2:
                            location.playSound(XSound.of(args[0]).orElse(XSound.MUSIC_DISC_13).get(), Parser.FLOAT.convert(args[1]), 1);
                            return;
                        case 3:
                            location.playSound(XSound.of(args[0]).orElse(XSound.MUSIC_DISC_13).get(), Parser.FLOAT.convert(args[1]), Parser.FLOAT.convert(args[2]));
                            return;
                        default:
                            throw new ActionException("SOUND", "Incorrectly amount of args: " + args.length + ". The right way to use is [SOUND] sound, Optional:[volume, Optional:[pitch]].");
                    }
                } catch (Throwable e) {
                    throw new ActionException("SOUND", "Unable to convert text in values, check if the values are correct. sound: TEXT (ENUM NAME), volume: DECIMAL, pitch: DECIMAL");
                }
            }
    ),
    GAMEMODE(
            "[GAMEMODE] gamemode",
            "Sets the gamemode of a player",
            "COMPASS",
            true,
            (player, args) -> {

                String gamemode = args[0].toUpperCase();

                try {
                    Parser.INTEGER.convert(gamemode);
                    player.setGameMode(GameMode.getByValue(Integer.parseInt(gamemode)));
                    return;
                } catch (Exception ignored) {
                    player.setGameMode(GameMode.valueOf(gamemode.toUpperCase()));
                }
            }
    ),
    PARTICLE(
            "[PARTICLE] particle, Optional:[amount, Optional:[offset x, offset y, offset z]]",
            "Displays a particle to the player",
            "REDSTONE",
            true,
            (player, args) -> {
                try {
                    switch (args.length) {
                        case 1:
                            player.spawnParticle(XParticle.valueOf(args[0].toUpperCase()).get(), player.getLocation(),1);
                            return;
                        case 2:
                            player.spawnParticle(XParticle.valueOf(args[0].toUpperCase()).get(), player.getLocation(), Parser.INTEGER.convert(args[1]));
                            return;
                        case 5:
                            player.spawnParticle(XParticle.valueOf(args[0].toUpperCase()).get(), player.getLocation(), Parser.INTEGER.convert(args[1]), Parser.DOUBLE.convert(args[2]), Parser.DOUBLE.convert(args[3]), Parser.DOUBLE.convert(args[4]));
                            return;
                        default:
                            throw new ActionException("PARTICLE", "Incorrectly amount of args: " + args.length + ". The right way to use is [PARTICLE] particle, Optional:[amount, Optional:[offset x, offset y, offset z]].");
                    }
                } catch (Throwable e) {
                    throw new ActionException("PARTICLE", "Unable to convert text in values, check if the values are correct. particle: TEXT (ENUM NAME), amount: INTEGER, offset x: DECIMAL, offset y: DECIMAL, offset z: DECIMAL");
                }
            }
    ),
    FIREWORK(
            "[FIREWORK] type, color, colorfade, trail, flicker, power",
            "Spawns a firework to a player",
            "FIREWORK_ROCKET",
            true,
            (player, args) -> {
                try {
                    if (args.length != 6) {
                        throw new ActionException("FIREWORK", "Incorrectly amount of args: " + args.length + ". The right way to use is [FIREWORK] type, color, color fade, trail, flicker, power.");
                    }
                    Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), XEntityType.FIREWORK_ROCKET.get());

                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    builder.with(FireworkEffect.Type.valueOf(args[0]));
                    builder.withColor(Color.fromRGB(Integer.parseInt(args[1].replace("#", ""), 16)));
                    builder.withFade(Color.fromRGB(Integer.parseInt(args[2].replace("#", ""), 16)));
                    builder.trail(Parser.BOOLEAN.convert(args[3]));
                    builder.flicker(Parser.BOOLEAN.convert(args[4]));

                    FireworkEffect effect = builder.build();
                    fireworkMeta.addEffect(effect);
                    fireworkMeta.setPower(Parser.INTEGER.convert(args[5]));

                    firework.setFireworkMeta(fireworkMeta);

                    try {
                        firework.detonate();
                    } catch (Exception ignored) {}
                } catch (Throwable e) {
                    throw new ActionException("FIREWORK", "Unable to convert text in values, check if the values are correct. type: TEXT: (ENUM NAME), color: HEXADECIMAL, color fade: HEXADECIMAL, trail: BOOLEAN, flicker: BOOLEAN, power: INTEGER");
                }

            }
    ),
    CLEAR_CHAT(
            "[CLEAR_CHAT]",
            "Clears the chat",
            "NAME_TAG",
            false,
            (player, args) -> IntStream.range(0, 100).mapToObj(i -> " ").forEach(e -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(e)))
    ),
    CLEAR_INVENTORY(
            "[CLEAR_INVENTORY] ",
            "Clears the player inventory",
            "BARRIER",
            false,
            (player, args) -> {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
            }
    ),
    EQUIP(
            "[EQUIP] equipslot, material",
            "Equip an item",
            "IRON_CHESTPLATE",
            true,
            (player, args) -> {
                Slot slot = Slot.valueOf(args[0].toUpperCase());
                XMaterial material = XMaterial.valueOf(args[1]);
                switch (slot) {
                    case HELMET:
                        player.getInventory().setHelmet(material.parseItem());
                        break;
                    case CHESTPLATE:
                        player.getInventory().setChestplate(material.parseItem());
                        break;
                    case LEGGINGS:
                        player.getInventory().setLeggings(material.parseItem());
                        break;
                    case BOOTS:
                        player.getInventory().setBoots(material.parseItem());
                        break;
                    case OFFHAND:
                        if (MinecraftVersion.isOlderThan(9)) {
                            Debug.of(XG7Lobby.getInstance()).severe("The offhand slot is only available in 1.9 or higher.");
                            return;
                        }
                        player.getInventory().setItemInOffHand(material.parseItem());
                        break;
                }
            }
    ),
    LOBBY("[LOBBY] id",
            "Teleport to a lobby",
            "BLAZE_ROD",
            true,
            (player, args) -> {
                if (args.length != 1) throw new ActionException("LOBBY", "Incorrectly amount of args: " + args.length + ". The right way to use is [LOBBY] lobbyId.");

                String id = args[0];

                if (id.equalsIgnoreCase("random")) {
                    XG7LobbyAPI.requestRandomLobbyLocation().thenAccept(l -> {
                        if (l == null) return;
                        l.teleport(player);
                    });
                    return;
                }

                XG7LobbyAPI.requestLobbyLocation(args[0]).thenAccept(l -> {
                    if (l == null) return;
                    l.teleport(player);
                });
            }),
    BUNGEE(
            "[BUNGEE] server",
            "Connect to a bungee server",
            "ENDER_PEARL",
            true, (player, args) -> {
                if (args.length != 1)
                    throw new ActionException("BUNGEE", "Incorrectly amount of args: " + args.length + ". The right way to use is [BUNGEE] server.");
                try {
                    XG7PluginsAPI.getServerInfo().connectTo(args[0], player);
                } catch (IOException e) {
                    Debug.of(XG7Lobby.getInstance()).severe("Error while connecting to the server: " + args[0]);
                    throw new RuntimeException(e);
                }
            }
    ),
    OPEN_MENU(
            "[OPEN_MENU] menu_id",
            "Opens a custom inventory",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmZiOTJlYTJiZDlhNzdhZmJkM2YxNzAzODVhNTdjZGVkNzQ5YWIxNjAxODE0NTkzZTVkZTljYWQ5NTQ5NTkyYyJ9fX0=",
            true,
            (player, args) -> {
                if (args.length != 1)
                    throw new ActionException("OPEN", "Incorrectly amount of args: " + args.length + ". The right way to use is [OPEN_MENU] menu_id.");

                XG7LobbyAPI.customInventoryManager().openMenu(args[0], player);
            }
    ),
    CLOSE_MENU(
            "[CLOSE_MENU] ",
            "Closes the inventory",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRjMzZjOWNiNTBhNTI3YWE1NTYwN2EwZGY3MTg1YWQyMGFhYmFhOTAzZThkOWFiZmM3ODI2MDcwNTU0MGRlZiJ9fX0=",
            false,
            (player, args) -> player.closeInventory()
    ),
    OPEN_FORM(
            "[OPEN_FORM] form_id",
            "Sends a form",
            "BEDROCK",
            true,
            (player, args) -> {
                if (args.length != 1)
                    throw new ActionException("OPEN_FORM", "Incorrectly amount of args: " + args.length + ". The right way to use is [OPEN_FORM] form_id.");

                XG7LobbyAPI.customFormsManager().sendForm(args[0], player);
            }
    ),
    REFRESH_MENU(
            "[REFRESH_MENU] ",
            "Refreshs the menu",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UwNzIzNTVhZmE2MTQyYmFmNTY2MGM5MDY4N2M4YzUwZGM2M2U1Nzc4MWRkYmNhNWNlM2YzNTU0ZDFlMzc1ZSJ9fX0=",
            false,
            (player, args) -> {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof BasicMenuHolder) {
                    BasicMenu.refresh((MenuHolder) player.getOpenInventory().getTopInventory().getHolder());
                }
                if (XG7Menus.hasPlayerMenuHolder(player.getUniqueId())) {
                    BasicMenu.refresh(XG7Menus.getPlayerMenuHolder(player.getUniqueId()));
                }
            }
            ),
    SWAP(
            "[SWAP] itempath",
            "Swaps a item on the inventory",
            "EMERALD_BLOCK",
            true,
            ((player, args) -> {
                if (args.length != 3) {
                    throw new ActionException("SWAP", "Incorrectly amount of args: " + args.length + ". The right way to use is [SWAP] menuId, slot, itemPath.");
                }

                CustomInventoryManager inventoryManager = XG7LobbyAPI.customInventoryManager();

                LobbyInventory menu = inventoryManager.isGUI(args[0].substring(args[0].indexOf(":") + 1)) ? inventoryManager.getGUIByXG7MenusId(args[0]) : inventoryManager.getHotbarByXG7MenusId(args[0]);

                if (menu == null) {
                    throw new ActionException("SWAP", "The menu with id: " + args[0] + " doesn't exist.");
                }

                int slot = Parser.INTEGER.convert(args[1]);

                if (menu instanceof LobbyGUI) {

                    LobbyGUI lobbyGUI = (LobbyGUI) menu;

                    LobbyItem item = lobbyGUI.items().get(args[2]);

                    MenuHolder holder = (MenuHolder) player.getOpenInventory().getTopInventory().getHolder();

                    if (item == null) {
                        throw new ActionException("SWAP", "The item with path: " + args[1] + " doesn't exist in the menu with id: " + args[0]);
                    }

                    holder.getInventoryUpdater().setItem(com.xg7plugins.modules.xg7menus.Slot.fromSlot(slot), item.getItem());
                    return;
                }

                LobbyHotbar lobbyHotbar = (LobbyHotbar) menu;

                LobbyItem item = lobbyHotbar.items().get(args[2]);

                PlayerMenuHolder holder = XG7Menus.getInstance().getPlayerMenuHolder(player.getUniqueId());

                if (item == null) {
                    throw new ActionException("SWAP", "The item with path: " + args[1] + " doesn't exist in the menu with id: " + args[0]);
                }

                holder.getInventoryUpdater().setItem(com.xg7plugins.modules.xg7menus.Slot.fromSlot(slot), item.getItem());
            })

    ),
    HIDE_PLAYERS(
            "[HIDE_PLAYERS] ",
            "Hides the players on lobby",
            "ENDER_PEARL",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setHidingPlayers(true);
                    lobbyPlayer.applyHide();
                });
            }),
    SHOW_PLAYERS(
            "[SHOW_PLAYERS] ",
            "Shows the player on lobby",
            "ENDER_EYE",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setHidingPlayers(false);
                    lobbyPlayer.applyHide();
                });
            }),
    FLY_ON(
            "[FLY_ON] ",
            "Enables the player flying on lobby",
            "FEATHER",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setFlying(true);
                    lobbyPlayer.fly();
                });
            }
    ),
    FLY_OFF(
            "[FLY_FF] ",
            "Disables the player flying on lobby",
            "FEATHER",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setFlying(false);
                    lobbyPlayer.fly();
                });
            }
    ),
    BUILD_ON(
            "[BUILD_ON] ",
            "Enables the player building on lobby",
            "IRON_SHOVEL",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setBuildEnabled(true);
                    lobbyPlayer.applyBuild();
                });
            }
    ),
    BUILD_OFF(
            "[BUILD_FF] ",
            "Disables the player building on lobby",
            "IRON_SHOVEL",
            false,
            (player, args) -> {
                XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
                    lobbyPlayer.setBuildEnabled(false);
                    lobbyPlayer.applyBuild();
                });
            }
    );

    private final String usage;
    private final String description;
    private final String icon;
    private final boolean needArgs;
    private final BiConsumer<Player, String[]> action;

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

    public static Item actionItem(ActionType type) {
        Item item = Item.from(type.getIcon());

        item.name("lang:[help.menu.actions-menu.action-item.name]");
        item.lore(Arrays.asList("lang:[help.menu.actions-menu.action-item.lore.description]", "lang:[help.menu.actions-menu.action-item.lore.usage]"));

        item.setBuildPlaceholders(
                Pair.of("id", type.name().toLowerCase()),
                Pair.of("description", type.getDescription()),
                Pair.of("usage", type.getUsage())
        );

        return item;

    }

    private enum Slot {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        OFFHAND
    }

}
