package com.xg7plugins.xg7lobby.commands.entities;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.modules.xg7holograms.event.ClickAction;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.holograms.HologramsManager;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologramLine;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;
        String id = args.get(0, String.class);

        if (hologramsManager.existsHologram(id)) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-already-exists", Pair.of("id", id));
        }

        Location location = Location.fromPlayer(player);
        LobbyHologram hologram = new LobbyHologram(id);
        hologram.setLocation(location);
        hologram.addLine(new LobbyHologramLine("First line"));
        hologram.addLine(new LobbyHologramLine("Use \"/holograms line edit\" to edit the lines"));
        hologram.addLine(new LobbyHologramLine("Use \"/holograms actions list\" to see the click actions"));
        hologram.getDefaultClickActions().add("[MESSAGE] You clicked the hologram!");

        hologramsManager.registerHologram(hologram);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.created", Pair.of("id", id));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "delete",
            syntax = "/holograms delete <id>",
            description = "Deletes a hologram",
            permission = "xg7lobby.command.holograms.delete",
            iconMaterial = XMaterial.REDSTONE
    )
    public CommandState delete(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        String id = args.get(0, String.class);
        CommandState validation = validateHologram(id);
        if (validation != null) return validation;

        hologramsManager.unregisterHologram(id);
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.deleted", Pair.of("id", id));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "teleport",
            syntax = "/holograms teleport <id>",
            description = "Teleports to a hologram",
            permission = "xg7lobby.command.holograms.teleport",
            iconMaterial = XMaterial.ENDER_PEARL,
            isPlayerOnly = true
    )
    public CommandState teleport(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        String id = args.get(0, String.class);
        CommandState validation = validateHologram(id);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(id);
        Player player = (Player) sender;

        hologram.setLocation(Location.fromPlayer(player));
        hologramsManager.respawnHologram(id);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.teleported", Pair.of("id", id));

        return CommandState.FINE;
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
            syntax = "/holograms lines add <hologramId> <type|lineContent> [lineContent]",
            description = "Adds a line to a hologram",
            permission = "xg7lobby.command.holograms.lines.add",
            iconMaterial = XMaterial.EMERALD,
            depth = 2
    )
    public CommandState lineAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        Pair<LobbyHologramLine.Type, String> lineData = parseLineContent(args, 1);

        LobbyHologramLine line = new LobbyHologramLine(lineData.getSecond());
        line.setType(lineData.getFirst());
        hologram.addLine(line);

        hologramsManager.respawnHologram(hologramId);
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.line.added", Pair.of("id", hologramId));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "remove",
            parent = "lines",
            syntax = "/holograms lines remove <hologramId> <lineIndex>",
            description = "Removes a line from a hologram",
            permission = "xg7lobby.command.holograms.lines.remove",
            iconMaterial = XMaterial.RED_WOOL,
            depth = 2
    )
    public CommandState lineRemove(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        int lineIndex = args.get(1, Integer.class) - 1;

        validation = validateLine(hologram, lineIndex, hologramId);
        if (validation != null) return validation;

        hologram.removeLine(lineIndex);
        hologramsManager.respawnHologram(hologramId);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.line.removed", Pair.of("id", hologramId));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "edit",
            parent = "lines",
            syntax = "/holograms lines edit <hologramId> <lineIndex> <type|newContent> [newContent]",
            description = "Edits a line of a hologram",
            permission = "xg7lobby.command.holograms.lines.edit",
            iconMaterial = XMaterial.OAK_PLANKS,
            depth = 2
    )
    public CommandState lineEdit(CommandSender sender, CommandArgs args) {
        if (args.len() < 3) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        int lineIndex = args.get(1, Integer.class) - 1;

        validation = validateLine(hologram, lineIndex, hologramId);
        if (validation != null) return validation;

        Pair<LobbyHologramLine.Type, String> lineData = parseLineContent(args, 2);

        LobbyHologramLine line = hologram.getLines().get(lineIndex);
        line.setType(lineData.getFirst());
        line.setContent(lineData.getSecond());

        hologramsManager.respawnHologram(hologramId);
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.line.edited", Pair.of("id", hologramId));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "options",
            parent = "lines",
            syntax = "/holograms lines options <hologramId> <lineIndex> <optionKey> <optionValue>",
            description = "Sets options for a hologram line",
            permission = "xg7lobby.command.holograms.lines.options",
            iconMaterial = XMaterial.CLOCK,
            depth = 2
    )
    public CommandState lineOptions(CommandSender sender, CommandArgs args) {
        if (args.len() < 4) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        int lineIndex = args.get(1, Integer.class) - 1;

        validation = validateLine(hologram, lineIndex, hologramId);
        if (validation != null) return validation;

        String optionKey = args.get(2, String.class);
        Object optionValue = parseValue(args.get(3, String.class));

        LobbyHologramLine line = hologram.getLines().get(lineIndex);
        line.getOptions().put(optionKey, optionValue);

        hologramsManager.respawnHologram(hologramId);
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.line.options-set",
                Pair.of("id", hologramId),
                Pair.of("key", optionKey),
                Pair.of("value", optionValue.toString())
        );

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "equip",
            parent = "lines",
            syntax = "/holograms lines equip <hologramId> <lineIndex> <equipmentSlot>",
            description = "Equips an item to a hologram line",
            permission = "xg7lobby.command.holograms.lines.equip",
            iconMaterial = XMaterial.IRON_CHESTPLATE,
            depth = 2,
            isPlayerOnly = true
    )
    public CommandState lineEquip(CommandSender sender, CommandArgs args) {
        if (args.len() < 3) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;
        String hologramId = args.get(0, String.class);

        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        int lineIndex = args.get(1, Integer.class) - 1;

        validation = validateLine(hologram, lineIndex, hologramId);
        if (validation != null) return validation;

        EquipmentSlot slot;
        try {
            slot = EquipmentSlot.valueOf(args.get(2, String.class).toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-line-invalid-equipment-slot",
                    Pair.of("slot", args.get(2, String.class)));
        }

        LobbyHologramLine line = hologram.getLines().get(lineIndex);
        Item item = player.getItemInHand() == null ? Item.air() : Item.from(player.getItemInHand().clone());
        line.getEquipment().put(slot, item);

        hologramsManager.respawnHologram(hologramId);
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.line.equipped", Pair.of("id", hologramId));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "actions",
            syntax = "/holograms actions <id|add|remove|edit|list> ...",
            description = "Manage hologram click actions",
            permission = "xg7lobby.command.holograms.actions",
            iconMaterial = XMaterial.REDSTONE_TORCH
    )
    public CommandState actions(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.actions.showing", Pair.of("id", hologramId));

        displayActions(sender, "&7-- &eDEFAULT: ", hologram.getDefaultClickActions());
        displayActions(sender, "&7-- &eRIGHT CLICK: ", hologram.getRightClickActions());
        displayActions(sender, "&7-- &eLEFT CLICK: ", hologram.getLeftClickActions());
        displayActions(sender, "&7-- &eSHIFT RIGHT CLICK: ", hologram.getShiftRightClickActions());
        displayActions(sender, "&7-- &eSHIFT LEFT CLICK: ", hologram.getShiftLeftClickActions());

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "add",
            parent = "actions",
            syntax = "/holograms actions add <hologramId> <action|clickType> <actionString>",
            description = "Adds a click action to a hologram",
            permission = "xg7lobby.command.holograms.actions.add",
            iconMaterial = XMaterial.EMERALD,
            depth = 2
    )
    public CommandState actionAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        ClickAction actionType = args.get(1, ClickAction.class);
        String actionString = actionType == null ? args.join(1) : args.join(2);

        getActionsList(hologram, actionType).add(actionString);
        hologramsManager.respawnHologram(hologramId);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.action.added",
                Pair.of("id", hologramId),
                Pair.of("clickType", actionType != null ? actionType.name() : "DEFAULT"));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "remove",
            parent = "actions",
            syntax = "/holograms actions remove <hologramId> <clickType|index> [index]",
            description = "Removes a click action from a hologram",
            permission = "xg7lobby.command.holograms.actions.remove",
            iconMaterial = XMaterial.RED_WOOL,
            depth = 2
    )
    public CommandState actionRemove(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        ClickAction actionType = args.get(1, ClickAction.class);
        int index = (actionType != null ? args.get(2, Integer.class) : args.get(1, Integer.class)) - 1;

        List<String> actions = getActionsList(hologram, actionType);

        if (index < 0 || index >= actions.size()) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-action-not-found",
                    Pair.of("id", hologramId),
                    Pair.of("index", String.valueOf(index + 1)));
        }

        actions.remove(index);
        hologramsManager.respawnHologram(hologramId);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.action.removed",
                Pair.of("id", hologramId),
                Pair.of("index", String.valueOf(index + 1)),
                Pair.of("clickType", actionType != null ? actionType.name() : "DEFAULT"));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "edit",
            parent = "actions",
            syntax = "/holograms actions edit <hologramId> <index> <action|clickType> <actionString>",
            description = "Edits a click action of a hologram",
            permission = "xg7lobby.command.holograms.actions.edit",
            iconMaterial = XMaterial.OAK_PLANKS,
            depth = 2
    )
    public CommandState actionEdit(CommandSender sender, CommandArgs args) {
        if (args.len() < 3) {
            return CommandState.SYNTAX_ERROR;
        }

        String hologramId = args.get(0, String.class);
        CommandState validation = validateHologram(hologramId);
        if (validation != null) return validation;

        LobbyHologram hologram = hologramsManager.getHologram(hologramId);
        int actionIndex = args.get(1, Integer.class) - 1;
        ClickAction actionType = args.get(2, ClickAction.class);
        String actionString = actionType == null ? args.join(2) : args.join(3);

        List<String> actions = getActionsList(hologram, actionType);

        if (actionIndex < 0 || actionIndex >= actions.size()) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-action-not-found",
                    Pair.of("id", hologramId),
                    Pair.of("index", String.valueOf(actionIndex + 1)));
        }

        actions.set(actionIndex, actionString);
        hologramsManager.respawnHologram(hologramId);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.holograms.action.edited",
                Pair.of("id", hologramId),
                Pair.of("clickType", actionType != null ? actionType.name() : "DEFAULT"));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "list",
            parent = "actions",
            syntax = "/holograms actions list <hologramId>",
            description = "Lists click actions of a hologram",
            permission = "xg7lobby.command.holograms.actions.list",
            iconMaterial = XMaterial.PAPER,
            depth = 2
    )
    public CommandState actionList(CommandSender sender, CommandArgs args) {
        return actions(sender, args);
    }

    private CommandState validateHologram(String id) {
        if (!hologramsManager.existsHologram(id)) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-not-found", Pair.of("id", id));
        }
        return null;
    }

    private CommandState validateLine(LobbyHologram hologram, int lineIndex, String hologramId) {
        if (lineIndex < 0 || lineIndex >= hologram.getLines().size()) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-line-not-found",
                    Pair.of("id", hologramId),
                    Pair.of("index", String.valueOf(lineIndex + 1)));
        }
        return null;
    }

    private List<String> getActionsList(LobbyHologram hologram, ClickAction actionType) {
        if (actionType == null) {
            return hologram.getDefaultClickActions();
        }

        switch (actionType) {
            case RIGHT_CLICK:
                return hologram.getRightClickActions();
            case LEFT_CLICK:
                return hologram.getLeftClickActions();
            case SHIFT_RIGHT:
                return hologram.getShiftRightClickActions();
            case SHIFT_LEFT:
                return hologram.getShiftLeftClickActions();
            default:
                return hologram.getDefaultClickActions();
        }
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

    private void displayActions(CommandSender sender, String header, List<String> actions) {
        Text.send(header, sender);
        actions.forEach(action -> Text.send("&a- &f" + action, sender));
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
}