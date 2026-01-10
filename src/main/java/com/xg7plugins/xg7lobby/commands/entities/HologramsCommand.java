package com.xg7plugins.xg7lobby.commands.entities;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionType;
import com.xg7plugins.xg7lobby.holograms.HologramEditor;
import com.xg7plugins.xg7lobby.holograms.HologramsManager;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologramLine;
import com.xg7plugins.xg7lobby.npcs.NPCEditor;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//It is a little bit long, but it is okay for a complex command class :D

@CommandSetup(
        name = "holograms",
        description = "Main command for holograms management",
        syntax = "/holograms <create|delete|teleport|lines|actions|save> ...",
        permission = "xg7lobby.command.holograms",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.ARMOR_STAND
)
@AllArgsConstructor
public class HologramsCommand implements Command {

    private final HologramsManager hologramsManager;
    private final HologramEditor editor = new HologramEditor();

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender) {
        return CommandState.SYNTAX_ERROR;
    }

    @CommandConfig(
            name = "create",
            syntax = "/holograms create <id>",
            description = "Creates a new hologram",
            permission = "xg7lobby.command.holograms.create",
            iconMaterial = XMaterial.EMERALD,
            isPlayerOnly = true
    )
    public CommandState create(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;
        String id = args.get(0, String.class);

        if (hologramsManager.existsHologram(id)) return CommandState.error(XG7Lobby.getInstance(), "hologram-already-exists", Pair.of("id", id));

        Location location = Location.fromPlayer(player);
        LobbyHologram hologram = new LobbyHologram(id);

        editor.select(hologram);
        editor.teleport(location);

        editor.addLine(LobbyHologramLine.Type.ARMOR_STAND, Text.fromLang(sender, XG7Lobby.getInstance(), "commands.holograms.defaults.first-line").getText());
        editor.addLine(LobbyHologramLine.Type.ARMOR_STAND, Text.fromLang(sender, XG7Lobby.getInstance(), "commands.holograms.defaults.second-line").getText());
        editor.addLine(LobbyHologramLine.Type.ARMOR_STAND, Text.fromLang(sender, XG7Lobby.getInstance(), "commands.holograms.defaults.third-line").getText());
        editor.addAction(Text.fromLang(sender, XG7Lobby.getInstance(), "commands.holograms.defaults.first-action").getText());

        hologramsManager.registerHologram(hologram);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.created", Pair.of("id", id));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "select",
            syntax = "/holograms select <id>",
            description = "Selects a hologram for editing",
            permission = "xg7lobby.command.holograms.select",
            iconMaterial = XMaterial.DIAMOND,
            isPlayerOnly = true
    )
    public CommandState select(CommandSender sender, CommandArgs args) {
        if  (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }
        String id = args.get(0, String.class);
        if (!hologramsManager.existsHologram(id)) return CommandState.error(XG7Lobby.getInstance(), "hologram-not-found", Pair.of("id", id));

        LobbyHologram hologram = hologramsManager.getHologram(id);
        editor.select(hologram);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.selected", Pair.of("id", id));
        return CommandState.FINE;
    }

    @CommandConfig(
            name = "delete",
            syntax = "/holograms delete",
            description = "Deletes a hologram",
            permission = "xg7lobby.command.holograms.delete",
            iconMaterial = XMaterial.REDSTONE
    )
    public CommandState delete(CommandSender sender) {
        Pair<HologramEditor.EditError, String> result = editor.remove();
        return edit(sender, result.getFirst(), -1, result.getSecond(), "commands.holograms.deleted", hologramsManager::unregisterHologram);
    }

    @CommandConfig(
            name = "teleport",
            syntax = "/holograms teleport",
            description = "Teleports to a hologram",
            permission = "xg7lobby.command.holograms.teleport",
            iconMaterial = XMaterial.ENDER_PEARL,
            isPlayerOnly = true
    )
    public CommandState teleport(CommandSender sender) {

        Player player = (Player) sender;
        HologramEditor.EditError error = editor.teleport(Location.fromPlayer(player));

        return edit(sender, error, "commands.holograms.teleported", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "save",
            syntax = "/holograms save",
            description = "Saves all holograms to disk",
            permission = "xg7lobby.command.holograms.save",
            iconMaterial = XMaterial.DIAMOND,
            isAsync = true
    )
    public CommandState save(CommandSender sender) {
        hologramsManager.saveHolograms().join();
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.saved");
        return CommandState.FINE;
    }

    @CommandConfig(
            name = "lines",
            syntax = "/holograms lines <add|edit|remove|options|equip> ...",
            description = "Manage hologram lines",
            permission = "xg7lobby.command.holograms.lines",
            iconMaterial = XMaterial.BOOK
    )
    public CommandState lines(Command sender) {
        return CommandState.SYNTAX_ERROR;
    }

    @CommandConfig(
            name = "add",
            parent = "lines",
            syntax = "/holograms lines add <type|lineContent> [lineContent]",
            description = "Adds a line to a hologram",
            permission = "xg7lobby.command.holograms.lines.add",
            iconMaterial = XMaterial.EMERALD,
            depth = 2
    )
    public CommandState lineAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Pair<LobbyHologramLine.Type, String> lineData;
        try {
            lineData = parseLineContent(args, 0);
        } catch (Exception e) {
            return CommandState.SYNTAX_ERROR;
        }

        return edit(sender, editor.addLine(lineData.getFirst(), lineData.getSecond()), "commands.holograms.lines.added", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "remove",
            parent = "lines",
            syntax = "/holograms lines remove <lineIndex>",
            description = "Removes a line from a hologram",
            permission = "xg7lobby.command.holograms.lines.remove",
            iconMaterial = XMaterial.RED_WOOL,
            depth = 2
    )
    public CommandState lineRemove(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        return editLine(sender, editor.removeLine(lineIndex), lineIndex,"commands.holograms.lines.removed", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "edit",
            parent = "lines",
            syntax = "/holograms lines edit <lineIndex> <type|newContent> [newContent]",
            description = "Edits a line of a hologram",
            permission = "xg7lobby.command.holograms.lines.edit",
            iconMaterial = XMaterial.OAK_PLANKS,
            depth = 2
    )
    public CommandState lineEdit(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        Pair<LobbyHologramLine.Type, String> lineData;
        try {
            lineData = parseLineContent(args, 1);
        } catch (Exception e) {
            return CommandState.SYNTAX_ERROR;
        }

        return editLine(sender, editor.editLine(lineIndex, lineData.getFirst(), lineData.getSecond()), lineIndex,"commands.holograms.lines.edited", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "options",
            parent = "lines",
            syntax = "/holograms lines options <lineIndex> <optionKey> <optionValue>",
            description = "Sets options for a hologram line",
            permission = "xg7lobby.command.holograms.lines.options",
            iconMaterial = XMaterial.CLOCK,
            depth = 2
    )
    public CommandState lineOptions(CommandSender sender, CommandArgs args) {
        if (args.len() != 3) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        String optionKey = args.get(1, String.class);
        Object optionValue = parseValue(args.get(2, String.class));

        return editLine(sender, editor.setOption(lineIndex, optionKey, optionValue), lineIndex,"commands.holograms.lines.options-set", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "equip",
            parent = "lines",
            syntax = "/holograms lines equip <lineIndex> <equipmentSlot>",
            description = "Equips an item to a hologram line",
            permission = "xg7lobby.command.holograms.lines.equip",
            iconMaterial = XMaterial.IRON_CHESTPLATE,
            depth = 2,
            isPlayerOnly = true
    )
    public CommandState lineEquip(CommandSender sender, CommandArgs args) {
        if (args.len() != 2) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;

        int lineIndex = args.get(0, Integer.class) - 1;
        EquipmentSlot slot;
        try {
            slot = EquipmentSlot.valueOf(args.get(1, String.class).toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-line-invalid-equipment-slot", Pair.of("slot", args.get(1, String.class)));
        }

        Item item = player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR) ? Item.air() : Item.from(player.getItemInHand().clone());

        return editLine(sender, editor.equip(lineIndex, slot, item), lineIndex,"commands.holograms.lines.equipped", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "actions",
            syntax = "/holograms actions <add|remove|edit|list> ...",
            description = "Manage hologram click actions",
            permission = "xg7lobby.command.holograms.actions",
            iconMaterial = XMaterial.REDSTONE_TORCH
    )
    public CommandState actions(CommandSender sender) {

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.actions.showing", Pair.of("id", editor.getLobbyHologram().getId()));

        Text.send("&7--", sender);
        editor.getActions().forEach(action -> Text.send("&a- &f" + action, sender));
        Text.send("&7--", sender);

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "add",
            parent = "actions",
            syntax = "/holograms actions add <actionString>",
            description = "Adds a click action to a hologram",
            permission = "xg7lobby.command.holograms.actions.add",
            iconMaterial = XMaterial.EMERALD,
            depth = 2
    )
    public CommandState actionAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }
        String actionString = args.toString();

        return edit(sender, editor.addAction(actionString), "commands.holograms.actions.added", hologramsManager::respawnHologram);

    }

    @CommandConfig(
            name = "remove",
            parent = "actions",
            syntax = "/holograms actions remove <index>",
            description = "Removes a click action from a hologram",
            permission = "xg7lobby.command.holograms.actions.remove",
            iconMaterial = XMaterial.RED_WOOL,
            depth = 2
    )
    public CommandState actionRemove(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        int index = args.get(0, Integer.class) - 1;

        return editAction(sender, editor.removeAction(index), index, "commands.holograms.actions.removed", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "edit",
            parent = "actions",
            syntax = "/holograms actions edit <index> <actionString>",
            description = "Edits a click action of a hologram",
            permission = "xg7lobby.command.holograms.actions.edit",
            iconMaterial = XMaterial.OAK_PLANKS,
            depth = 2
    )
    public CommandState actionEdit(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        int index = args.get(0, Integer.class) - 1;
        String actionString = args.join(1);

        return editAction(sender, editor.editAction(index, actionString), index, "commands.holograms.actions.edited", hologramsManager::respawnHologram);
    }

    @CommandConfig(
            name = "list",
            parent = "actions",
            syntax = "/holograms actions list",
            description = "Lists click actions of a hologram",
            permission = "xg7lobby.command.holograms.actions.list",
            iconMaterial = XMaterial.PAPER,
            depth = 2
    )
    public CommandState actionList(CommandSender sender) {
        return actions(sender);
    }

    private CommandState edit(CommandSender sender, HologramEditor.EditError error, int lineIndex, String lineErrorMessagePathId, String messagePath, Consumer<String> successCallback) {

        switch (error) {
            case HOLOGRAM_NOT_SELECTED:
                return CommandState.error(XG7Lobby.getInstance(), "hologram-not-selected");
            case INVALID_LINE_INDEX:
                return CommandState.error(XG7Lobby.getInstance(), lineErrorMessagePathId, Pair.of("index", String.valueOf(lineIndex + 1)), Pair.of("id", editor.getLobbyHologram().getId()));
            case NONE:

                String id = editor.getLobbyHologram() == null ? lineErrorMessagePathId : editor.getLobbyHologram().getId();

                successCallback.accept(id);
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), messagePath, Pair.of("id", id), Pair.of("index", String.valueOf(lineIndex + 1)));
                return CommandState.FINE;
        }
        return CommandState.FINE;
    }
    private CommandState edit(CommandSender sender, HologramEditor.EditError error, String messagePath, Consumer<String> successCallback) {
        return edit(sender, error, -1, "", messagePath, successCallback);
    }
    private CommandState editLine(CommandSender sender, HologramEditor.EditError error, int lineIndex, String messagePath, Consumer<String> successCallback) {
        return edit(sender, error, lineIndex, "hologram-line-not-found", messagePath, successCallback);
    }
    private CommandState editAction(CommandSender sender, HologramEditor.EditError error, int actionIndex, String messagePath, Consumer<String> successCallback) {
        return edit(sender, error, actionIndex, "hologram-action-not-found", messagePath, successCallback);
    }


    private Pair<LobbyHologramLine.Type, String> parseLineContent(CommandArgs args, int startIndex) {
        String typeString = args.get(startIndex, String.class);
        LobbyHologramLine.Type type = LobbyHologramLine.Type.fromString(typeString.replace("#", ""));

        boolean hasTypePrefix = typeString.startsWith("#");
        String content = (!hasTypePrefix || type == LobbyHologramLine.Type.INVISIBLE)
                ? args.join(startIndex)
                : args.join(startIndex + 1);

        return Pair.of(type, content);
    }

    private static Object parseValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {}

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {}

        return value;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, CommandArgs args) {

        if (args.len() < 2)  {
            return Command.super.onTabComplete(sender, args);
        }

        CommandNode root = XG7Lobby.getInstance().getCommandManager().getRootCommandNode("holograms");
        CommandNode cmd1 = root.getChild(args.get(0, String.class));

        if (cmd1 == null) {
            return Command.super.onTabComplete(sender, args);
        }

        switch (cmd1.getName().toLowerCase()) {
            case "save":
            case "teleport":
            case "delete":
                return Collections.emptyList();
            case "create":
                if (args.len() == 2) return Collections.singletonList("<id>");
                break;
            case "select":
                if (args.len() == 2) return new ArrayList<>(hologramsManager.getHolograms().keySet());
                break;
        }

        if (args.len() < 3) {
            return Command.super.onTabComplete(sender, args);
        }

        CommandNode cmd2 = cmd1.getChild(args.get(1, String.class));

        if  (cmd2 == null) {
            return  Command.super.onTabComplete(sender, args);
        }

        if (cmd2.getName().equalsIgnoreCase("list")) {
            return Collections.emptyList();
        }

        switch (cmd1.getName().toLowerCase()) {
            case "lines":
                if (cmd2.getName().equalsIgnoreCase("equip")) {
                    if (args.len() == 4) return Arrays.asList("MAIN_HAND", "OFF_HAND", "BOOTS", "LEGGINGS", "CHEST_PLATE", "HELMET", "BODY", "SADDLE");
                }
                if (cmd2.getName().equalsIgnoreCase("add") || (cmd2.getName().equalsIgnoreCase("edit") && args.len() == 4)) {
                    return Arrays.asList("#armor_stand", "#item", "#entity", "#display", "Your text here");
                }
                if (cmd2.getName().equalsIgnoreCase("remove") || cmd2.getName().equalsIgnoreCase("edit") || cmd2.getName().equalsIgnoreCase("equip")) {
                    if (args.len() == 3) return Arrays.asList("index", "1", "2");
                }
                break;
            case "actions":
                if (cmd2.getName().equalsIgnoreCase("remove") || cmd2.getName().equalsIgnoreCase("edit")) {
                    if (args.len() == 3) return Arrays.asList("index", "1", "2");
                }
                if (cmd2.getName().equalsIgnoreCase("add") && args.len() == 3 || (cmd2.getName().equalsIgnoreCase("edit") && args.len() == 4)) {
                    return Arrays.stream(ActionType.values()).map(t -> "[" + t.toString() + "]").collect(Collectors.toList());
                }
                break;
        }
        return null;
    }
}