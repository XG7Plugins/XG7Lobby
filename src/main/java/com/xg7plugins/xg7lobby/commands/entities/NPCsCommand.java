package com.xg7plugins.xg7lobby.commands.entities;

import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.xg7plugins.XG7Plugins;
import com.xg7plugins.commands.node.CommandConfig;
import com.xg7plugins.commands.node.CommandNode;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.setup.CommandSetup;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.skin.Skin;
import com.xg7plugins.utils.skin.SkinRequest;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionType;
import com.xg7plugins.xg7lobby.holograms.HologramEditor;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologramLine;
import com.xg7plugins.xg7lobby.npcs.LobbyNPC;
import com.xg7plugins.xg7lobby.npcs.NPCEditor;
import com.xg7plugins.xg7lobby.npcs.NPCsManager;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//It is a little bit long, but it is okay for a complex command class :D

@AllArgsConstructor
@CommandSetup(
        name = "npcs",
        description = "Main command for NPCs management",
        syntax = "/npcs <create|delete|tp|edit|type|glow|skin|options|equip|select|name|save>",
        permission = "xg7lobby.command.npcs",
        pluginClass = XG7Lobby.class,
        iconMaterial = XMaterial.PLAYER_HEAD
)
public class NPCsCommand implements Command {

    private final NPCsManager npcsManager;
    private final NPCEditor editor = new NPCEditor();

    @CommandConfig(isPlayerOnly = true)
    public CommandState onCommand(CommandSender sender) {
        return CommandState.SYNTAX_ERROR;
    }

    @CommandConfig(
            name = "create",
            description = "Create a new NPC",
            syntax = "/npcs create <id>",
            permission = "xg7lobby.command.npcs.create",
            iconMaterial = XMaterial.PLAYER_HEAD
    )
    public CommandState create(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        String id = args.get(0, String.class);
        Player player = (Player) sender;

        if (npcsManager.existsNPC(id)) return CommandState.error(XG7Lobby.getInstance(), "npc-already-exists", Pair.of("id", id));

        Location location = Location.fromPlayer(player);

        LobbyHologram npcName = new LobbyHologram(id + "_hologram");
        LobbyNPC npc = new LobbyNPC(id);

        npc.setHologram(npcName);

        editor.select(npc);
        editor.setLocation(location);
        editor.setType(LobbyNPC.NPCType.PLAYER, null);

        editor.getHologramEditor().addLine(LobbyHologramLine.Type.ARMOR_STAND, Text.fromLang(sender, XG7Lobby.getInstance(), "commands.npcs.first-name").getText());
        editor.addAction(Text.fromLang(sender, XG7Lobby.getInstance(), "commands.npcs.first-action").getText());

        npcsManager.registerNPC(npc);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.npcs.created", Pair.of("id", id));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "select",
            description = "Select an existing NPC",
            syntax = "/npcs select <id>",
            permission = "xg7lobby.command.npcs.select",
            iconMaterial = XMaterial.GOLD_INGOT
    )
    public CommandState select(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        String id = args.get(0, String.class);

        LobbyNPC npc = npcsManager.getNPC(id);
        if (npc == null) {
            return CommandState.error(XG7Lobby.getInstance(), "npc-not-found", Pair.of("id", id));
        }

        editor.select(npc);

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.npcs.selected", Pair.of("id", id));

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "delete",
            description = "Delete the selected NPC",
            syntax = "/npcs delete",
            permission = "xg7lobby.command.npcs.delete",
            iconMaterial = XMaterial.BARRIER
    )
    public CommandState delete(CommandSender sender) {
        Pair<NPCEditor.EditError, String> result = editor.remove();
        return edit(sender, result.getFirst(), result.getSecond(), -1, "commands.npcs.deleted", npcsManager::unregisterNPC);
    }

    @CommandConfig(
            name = "teleport",
            description = "Teleport to the selected NPC",
            syntax = "/npcs tp",
            permission = "xg7lobby.command.npcs.tp",
            iconMaterial = XMaterial.ENDER_PEARL,
            isPlayerOnly = true
    )
    public CommandState teleport(CommandSender sender) {
        return edit(sender, editor.setLocation(Location.fromPlayer((Player) sender)), "commands.npcs.teleported", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "glow",
            description = "Set glow effect for the selected NPC",
            syntax = "/npcs glow (true|false)",
            permission = "xg7lobby.command.npcs.glow",
            iconMaterial = XMaterial.GLOWSTONE
    )
    public CommandState glow(CommandSender sender, CommandArgs args) {

        boolean glow = !editor.getLobbyNPC().getOptions().getOrDefault("glow", false).equals(true);

        if (args.len() == 1) glow = args.get(0, Boolean.class);

        return edit(sender, editor.setGlow(glow), "commands.npcs.glow-set", npcsManager::respawnNPC);

    }

    @CommandConfig(
            name = "type",
            description = "Set type for the selected NPC",
            syntax = "/npcs type <type> [type options]",
            permission = "xg7lobby.command.npcs.type",
            iconMaterial = XMaterial.VILLAGER_SPAWN_EGG,
            isPlayerOnly = true
    )
    public CommandState type(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;

        LobbyNPC.NPCType type = args.get(0, LobbyNPC.NPCType.class);
        Object typeOptions = null;

        if (type == LobbyNPC.NPCType.MOB) {
            if (args.len() != 2) return CommandState.SYNTAX_ERROR;
            typeOptions = args.get(1, String.class);
        }

        if (type == LobbyNPC.NPCType.ITEM_DISPLAY) {
            Item item = Item.from(player.getItemInHand());
            if (item.isAir()) return CommandState.error(XG7Lobby.getInstance(), "npc-no-item-to-display");
            typeOptions = item;
        }

        return editType(sender, editor.setType(type, typeOptions), type.name(), "commands.npcs.type-set", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "skin",
            description = "Set skin for the selected NPC",
            syntax = "/npcs skin <url|player|uuid|reset>",
            permission = "xg7lobby.command.npcs.skin",
            iconMaterial = XMaterial.CREEPER_HEAD,
            isPlayerOnly = true
    )
    public CommandState skin(CommandSender sender, CommandArgs args) {
        return CommandState.SYNTAX_ERROR;
    }

    private CommandState setSkin(CommandSender sender, Skin skin) {
        return edit(sender, editor.setSkin(skin), "commands.npcs.skin-set", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "url",
            parent = "skin",
            description = "Set skin by an image URL for the selected NPC",
            syntax = "/npcs skin url <skin url>",
            permission = "xg7lobby.command.npcs.skin.url",
            iconMaterial = XMaterial.ENDER_CHEST,
            isPlayerOnly = true,
            isAsync = true,
            depth = 2
    )
    public CommandState skinByURL(CommandSender sender, CommandArgs args) {
        Skin skin = SkinRequest.requestSkinByImageURL(args.get(0, String.class)).join();
        if (skin == null) return CommandState.error(XG7Lobby.getInstance(), "npc-skin-url-invalid");
        return setSkin(sender, skin);
    }

    @CommandConfig(
            name = "uuid",
            parent = "skin",
            description = "Set skin by a player UUID for the selected NPC",
            syntax = "/npcs skin uuid <uuid>",
            permission = "xg7lobby.command.npcs.skin.uuid",
            iconMaterial = XMaterial.REDSTONE,
            isPlayerOnly = true,
            isAsync = true,
            depth = 2
    )
    public CommandState skinByUUID(CommandSender sender, CommandArgs args) {
        Skin skin = SkinRequest.requestSkinByUUID(args.get(0, UUID.class)).join();
        if (skin == null) return CommandState.error(XG7Lobby.getInstance(), "npc-skin-uuid-invalid");
        return setSkin(sender, skin);
    }

    @CommandConfig(
            name = "player",
            parent = "skin",
            description = "Set skin by a player name for the selected NPC",
            syntax = "/npcs skin player <player name>",
            permission = "xg7lobby.command.npcs.skin.player",
            iconMaterial = XMaterial.PLAYER_HEAD,
            isPlayerOnly = true,
            isAsync = true,
            depth = 2
    )
    public CommandState skinByPlayer(CommandSender sender, CommandArgs args) {

        OfflinePlayer offlinePlayer = args.get(0, OfflinePlayer.class);

        Skin skin = offlinePlayer.isOnline() ?
                SkinRequest.requestSkinByPlayer(offlinePlayer.getPlayer()).join() :
                SkinRequest.requestSkinByPlayerName(offlinePlayer.getName()).join();

        if (skin == null) return CommandState.error(XG7Lobby.getInstance(), "npc-skin-player-invalid");
        return setSkin(sender, skin);
    }

    @CommandConfig(
            name = "mirror",
            parent = "skin",
            description = "Set the skin to be a mirror of the player who is viewing the selected NPC.",
            syntax = "/npcs skin mirror (true|false)",
            permission = "xg7lobby.command.npcs.skin.mirror",
            iconMaterial = XMaterial.MAP,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState skinMirror(CommandSender sender, CommandArgs args) {

        boolean mirror = !editor.getLobbyNPC().getOptions().getOrDefault("render_players_skin", false).equals(true);

        if (args.len() == 1) mirror = args.get(0, Boolean.class);

        return edit(sender, editor.setOption("render_players_skin", mirror), "commands.npcs.skin-mirror-set", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "reset",
            parent = "skin",
            description = "Reset skin for the selected NPC",
            syntax = "/npcs skin reset",
            permission = "xg7lobby.command.npcs.skin.reset",
            iconMaterial = XMaterial.BARRIER,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState resetSkin(CommandSender sender) {
        return setSkin(sender, null);
    }

    @CommandConfig(
            name = "options",
            description = "Set a option value for the selected NPC",
            syntax = "/npcs options <option> <value>",
            permission = "xg7lobby.command.npcs.options",
            iconMaterial = XMaterial.COMMAND_BLOCK,
            isPlayerOnly = true
    )
    public CommandState setOption(CommandSender sender, CommandArgs args) {

        if (args.len() != 2) {
            return CommandState.SYNTAX_ERROR;
        }

        String optionKey = args.get(0, String.class);
        Object optionValue = parseValue(args.get(1, String.class));

        return editOption(sender, editor.setOption(optionKey, optionValue), -1, "commands.npcs.option-set", optionKey, optionValue.toString());
    }

    @CommandConfig(
            name = "name",
            description = "Edit the name (hologram) of the selected NPC",
            syntax = "/npcs name <addline|removeline|editline|equip> [arguments]",
            permission = "xg7lobby.command.npcs.name",
            iconMaterial = XMaterial.NAME_TAG,
            isPlayerOnly = true
    )
    public CommandState name(CommandSender sender, CommandArgs args) {
        return CommandState.SYNTAX_ERROR;
    }

    @CommandConfig(
            name = "addline",
            parent = "name",
            description = "Add a line to the NPC's name hologram",
            syntax = "/npcs name addline <lineType|text> <line content>",
            permission = "xg7lobby.command.npcs.name.addline",
            iconMaterial = XMaterial.LIME_WOOL,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState addLine(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Pair<LobbyHologramLine.Type, String> lineData;
        try {
            lineData = HologramsCommand.parseLineContent(args, 0);
        } catch (Exception e) {
            return CommandState.SYNTAX_ERROR;
        }

        return editHologramLine(sender, editor.getHologramEditor().addLine(lineData.getFirst(), lineData.getSecond()), -1,"commands.holograms.lines.added", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "options",
            parent = "name",
            description = "Set a value option to the NPC's name hologram",
            syntax = "/npcs name options <line index> <option> <value>",
            permission = "xg7lobby.command.npcs.name.options",
            iconMaterial = XMaterial.COMMAND_BLOCK,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState nameOptions(CommandSender sender, CommandArgs args) {
        if (args.len() != 3) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        String optionKey = args.get(1, String.class);
        Object optionValue = parseValue(args.get(2, String.class));

        return editOption(sender, editor.getHologramEditor().setOption(lineIndex, optionKey, optionValue), lineIndex, "commands.holograms.lines.options-set", optionKey, optionValue.toString());
    }

    @CommandConfig(
            name = "removeline",
            parent = "name",
            description = "Remove a line from the NPC's name hologram",
            syntax = "/npcs name removeline <line number>",
            permission = "xg7lobby.command.npcs.name.removeline",
            iconMaterial = XMaterial.RED_WOOL,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState removeLine(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        return editHologramLine(sender, editor.getHologramEditor().removeLine(lineIndex), lineIndex, "commands.holograms.lines.edited", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "editline",
            parent = "name",
            description = "Edit a line of the NPC's name hologram",
            syntax = "/npcs name editline <line number> <lineType|text> <new content>",
            permission = "xg7lobby.command.npcs.name.editline",
            iconMaterial = XMaterial.YELLOW_WOOL,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState editLine(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        int lineIndex = args.get(0, Integer.class) - 1;

        Pair<LobbyHologramLine.Type, String> lineData;
        try {
            lineData = HologramsCommand.parseLineContent(args, 1);
        } catch (Exception e) {
            return CommandState.SYNTAX_ERROR;
        }

        return editHologramLine(sender, editor.getHologramEditor().editLine(lineIndex, lineData.getFirst(), lineData.getSecond()), lineIndex, "commands.holograms.lines.edited", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "equip",
            parent = "name",
            description = "Equip an item to a slot in the NPC's name hologram",
            syntax = "/npcs name equip <line index> <slot>",
            permission = "xg7lobby.command.npcs.name.equip",
            iconMaterial = XMaterial.DIAMOND_CHESTPLATE,
            isPlayerOnly = true,
            depth = 2
    )
    public CommandState nameEquip(CommandSender sender, CommandArgs args) {
        if (args.len() != 2) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;

        int lineIndex = args.get(0, Integer.class) - 1;
        EquipmentSlot slot = args.get(1, EquipmentSlot.class);
        Item item = Item.from(player.getItemInHand());

        return editHologramLine(sender, editor.getHologramEditor().equip(lineIndex, slot, item), lineIndex, "commands.holograms.lines.equipped", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "equip",
            description = "Equip an item to a slot for the selected NPC",
            syntax = "/npcs equip <slot>",
            permission = "xg7lobby.command.name.equip",
            iconMaterial = XMaterial.DIAMOND_CHESTPLATE,
            isPlayerOnly = true
    )
    public CommandState equip(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) {
            return CommandState.SYNTAX_ERROR;
        }

        Player player = (Player) sender;
        EquipmentSlot slot = args.get(0, EquipmentSlot.class);
        Item item = Item.from(player.getItemInHand());

        return edit(sender, editor.equipItem(slot, item), "commands.npcs.equipped", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "actions",
            syntax = "/npcs actions <add|remove|edit|list> ...",
            description = "Manage selected NPC's click actions",
            permission = "xg7lobby.command.npcs.actions",
            iconMaterial = XMaterial.REDSTONE_TORCH
    )
    public CommandState actions(CommandSender sender) {

        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.npcs.actions.showing", Pair.of("id", editor.getLobbyNPC().getId()));

        Text.send("&7--", sender);
        editor.getActions().forEach(action -> Text.send("&a- &f" + action, sender));
        Text.send("&7--", sender);

        return CommandState.FINE;
    }

    @CommandConfig(
            name = "add",
            parent = "actions",
            syntax = "/npcs actions add <actionString>",
            description = "Adds a click action to the selected NPC",
            permission = "xg7lobby.command.npcs.actions.add",
            iconMaterial = XMaterial.EMERALD,
            depth = 2
    )
    public CommandState actionAdd(CommandSender sender, CommandArgs args) {
        if (args.len() < 1) return CommandState.SYNTAX_ERROR;

        String actionString = args.toString();

        return edit(sender, editor.addAction(actionString), "commands.npcs.actions.added", npcsManager::respawnNPC);

    }

    @CommandConfig(
            name = "remove",
            parent = "actions",
            syntax = "/npcs actions remove <index>",
            description = "Removes a click action from a NPC",
            permission = "xg7lobby.command.npcs.actions.remove",
            iconMaterial = XMaterial.RED_WOOL,
            depth = 2
    )
    public CommandState actionRemove(CommandSender sender, CommandArgs args) {
        if (args.len() != 1) return CommandState.SYNTAX_ERROR;

        int index = args.get(0, Integer.class) - 1;

        return editAction(sender, editor.removeAction(index), index, "commands.npcs.actions.removed", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "edit",
            parent = "actions",
            syntax = "/npcs actions edit <index> <actionString>",
            description = "Edits a click action of a NPC",
            permission = "xg7lobby.command.npcs.actions.edit",
            iconMaterial = XMaterial.OAK_PLANKS,
            depth = 2
    )
    public CommandState actionEdit(CommandSender sender, CommandArgs args) {
        if (args.len() < 2) {
            return CommandState.SYNTAX_ERROR;
        }

        int index = args.get(0, Integer.class) - 1;
        String actionString = args.join(1);

        return editAction(sender, editor.editAction(index, actionString), index, "commands.npcs.actions.edited", npcsManager::respawnNPC);
    }

    @CommandConfig(
            name = "list",
            parent = "actions",
            syntax = "/npcs actions list",
            description = "Lists click actions of a NPC",
            permission = "xg7lobby.command.npcs.actions.list",
            iconMaterial = XMaterial.PAPER,
            depth = 2
    )
    public CommandState actionList(CommandSender sender) {
        return actions(sender);
    }

    @CommandConfig(
            name = "save",
            description = "Save all NPCs to the disk",
            syntax = "/npcs save",
            permission = "xg7lobby.command.npcs.save",
            iconMaterial = XMaterial.CHEST,
            isPlayerOnly = true
    )
    public CommandState save(CommandSender sender) {
        npcsManager.saveNPCs();
        Text.sendTextFromLang(sender, XG7Lobby.getInstance(), "commands.npcs.saved");
        return CommandState.FINE;
    }



    private CommandState edit(CommandSender sender, NPCEditor.EditError error, String typeId, int lineIndex, String messagePath, Consumer<String> successCallback) {

        switch (error) {
            case NONE:
                String id = editor.getLobbyNPC() == null ? typeId : editor.getLobbyNPC().getId();
                if (successCallback != null) successCallback.accept(id);
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), messagePath, Pair.of("id", id), Pair.of("index", String.valueOf(lineIndex + 1)));
                return CommandState.FINE;
            case NPC_NOT_SELECTED:
                return CommandState.error(XG7Lobby.getInstance(), "npc-not-selected");
            case ILLEGAL_TYPE:
                return CommandState.error(XG7Lobby.getInstance(), "npc-illegal-type", Pair.of("type", typeId));
            case INVALID_ACTION_INDEX:
                return CommandState.error(XG7Lobby.getInstance(), "npc-invalid-action-index", Pair.of("index", String.valueOf(lineIndex + 1)));
        }

        return CommandState.FINE;

    }

    public CommandState editOption(CommandSender sender, Enum<?> error, int lineIndex, String messagePath, String optionKey, String optionValue) {

        if (error.name().equals("NONE")) {
            Text.sendTextFromLang(sender, XG7Lobby.getInstance(), messagePath,
                    Pair.of("id", editor.getLobbyNPC().getId()),
                    Pair.of("option", optionKey),
                    Pair.of("value", optionValue)
            );
            npcsManager.respawnNPC(editor.getLobbyNPC().getId());
            return CommandState.FINE;
        }

        if (error.name().equals("INVALID_LINE_INDEX")) {
            return CommandState.error(XG7Lobby.getInstance(), "hologram-line-not-found", Pair.of("id", editor.getLobbyNPC().getId()), Pair.of("index", String.valueOf(lineIndex + 1)));
        }

        return CommandState.error(XG7Lobby.getInstance(), "npc-not-selected");
    }

    public CommandState edit(CommandSender sender, NPCEditor.EditError error, String messagePath, Consumer<String> successCallback) {
       return edit(sender, error, "", -1, messagePath, successCallback);
    }

    public CommandState editAction(CommandSender sender, NPCEditor.EditError error, int actionIndex, String messagePath, Consumer<String> successCallback) {
        return edit(sender, error, "", actionIndex, messagePath, successCallback);
    }

    public CommandState editType(CommandSender sender, NPCEditor.EditError error, String type, String messagePath, Consumer<String> successCallback) {
        return edit(sender, error, type, -1, messagePath, successCallback);
    }

    public CommandState editHologramLine(CommandSender sender, HologramEditor.EditError error, int lineIndex, String messagePath, Consumer<String> successCallback) {
        switch (error) {
            case HOLOGRAM_NOT_SELECTED:
                return CommandState.error(XG7Lobby.getInstance(), "npc-not-selected");
            case INVALID_LINE_INDEX:
                return CommandState.error(XG7Lobby.getInstance(), "hologram-line-not-found", Pair.of("index", String.valueOf(lineIndex + 1)), Pair.of("id", editor.getLobbyNPC().getId()));
            case NONE:
                successCallback.accept(editor.getLobbyNPC().getId());
                Text.sendTextFromLang(sender, XG7Lobby.getInstance(), messagePath, Pair.of("id", editor.getLobbyNPC().getId()), Pair.of("index", String.valueOf(lineIndex + 1)));
                return CommandState.FINE;
        }
        return CommandState.FINE;
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


        CommandNode root = XG7Lobby.getInstance().getCommandManager().getRootCommandNode("xg7lobbynpcs");
        CommandNode cmd1 = root.getChild(args.get(0, String.class));

        if (cmd1 == null) {
            return Command.super.onTabComplete(sender, args);
        }

        switch (cmd1.getName().toLowerCase()) {
            case "delete":
            case "tp":
            case "save":
                return Collections.emptyList();
            case "glow":
                if (args.len() == 2) return Arrays.asList("true", "false");
                break;
            case "create":
                if (args.len() == 2) return Collections.singletonList("<id>");
                break;
            case "select":
                if (args.len() == 2) return new ArrayList<>(npcsManager.getNpcs().keySet());
                break;
            case "equip":
                if (args.len() == 2) return Arrays.asList("MAIN_HAND", "OFF_HAND", "BOOTS", "LEGGINGS", "CHEST_PLATE", "HELMET", "BODY", "SADDLE");
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
            case "skin":
                switch (cmd2.getName().toLowerCase()) {
                    case "url":
                        if (args.len() == 3) return Arrays.asList("http://", "https://");
                        break;
                    case "player":
                        if (args.len() == 3) return new ArrayList<>(XG7Plugins.getAPI().getAllPlayerNames());
                        break;
                    case "uuid":
                        if (args.len() == 3) return XG7Plugins.getAPI().getAllPlayerUUIDs().stream().map(UUID::toString).collect(Collectors.toList());
                        break;
                    case "mirror":
                        if (args.len() == 3) return Arrays.asList("true", "false");
                        break;
                }
            case "actions":
                if (cmd2.getName().equalsIgnoreCase("remove") || cmd2.getName().equalsIgnoreCase("edit")) {
                    if (args.len() == 3) return Arrays.asList("index", "1", "2");
                }
                if (cmd2.getName().equalsIgnoreCase("add") && args.len() == 3) {
                    return Arrays.stream(ActionType.values()).map(t -> "[" + t.toString() + "]").collect(Collectors.toList());
                }
                break;
            case "name":
                if (cmd2.getName().equalsIgnoreCase("equip")) {
                    if (args.len() == 4) return Arrays.asList("MAIN_HAND", "OFF_HAND", "BOOTS", "LEGGINGS", "CHEST_PLATE", "HELMET", "BODY", "SADDLE");
                }
                if (cmd2.getName().equalsIgnoreCase("addline") || (cmd2.getName().equalsIgnoreCase("editline") && args.len() == 4)) {
                    return Arrays.asList("#armor_stand", "#item", "#entity", "#display", "Your text here");
                }
                if (cmd2.getName().equalsIgnoreCase("removeline") || cmd2.getName().equalsIgnoreCase("editline") || cmd2.getName().equalsIgnoreCase("equip")) {
                    if (args.len() == 3) return Arrays.asList("index", "1", "2");
                }
                break;
        }
        return null;



    }

}
