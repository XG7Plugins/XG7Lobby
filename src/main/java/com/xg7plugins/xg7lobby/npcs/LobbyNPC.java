package com.xg7plugins.xg7lobby.npcs;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.xg7plugins.modules.xg7npcs.event.NPCClickEvent;
import com.xg7plugins.modules.xg7npcs.npc.NPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.DisplayNPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.MobNPC;
import com.xg7plugins.modules.xg7npcs.npc.impl.PlayerNPC;
import com.xg7plugins.utils.EntityDisplayOptions;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.location.Location;
import com.xg7plugins.utils.reflection.ReflectionClass;
import com.xg7plugins.utils.skin.Skin;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.holograms.data.LobbyHologram;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Data
public class LobbyNPC {

    private final String id;
    private Location location;

    private NPCType type;
    private Skin skin = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTczMDEzMjM2NjY3OCwKICAicHJvZmlsZUlkIiA6ICI3MGQzMzg2YzU5NzA0NmU1YWM4OTNhYmZlYTQ5N2IxMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJST1lMRUU1NDYwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyNjZlZTZiY2MyNjY1ZTBhODQ3N2Q0OTUzN2RkZjZiMjk4YjVjNGE1NDU2MWIyZjNjNDQ4MmI3N2IzNzA2MSIKICAgIH0KICB9Cn0=", "wOX7HhZ9VNtfNJi3GJFT3LnbXUCaFtpcyQDldoWvmrbA5RjrQ8H2jVcBXpMxnlk9U43KvNgFNxy/d3KklSNg9EfOBmo5H2GYICIx9iJOTCnOZC8GLhZWuia8jC7lqB6CfT7TdZWZAT2CXM2b8pteGWjoPg+OWUuXyg6Jg0k7uUrqzjMYjfh6y7hJXZIl38hMgISymrdQPGQVGTdBKeDmrQDveYn49ZYKdAbeb3pEHM5/QZIlvZVdvEHoLS4U5QRiw5V3/ERvd36RlKaydZVveqSMAoWvak/etVTiT3gLA5VbJN/qWYjz3rkmNboouYDC6eWy75b8TZSkPtk02JZ/ILDgpvYPyrAXwpZQNtWLXF99zun+aSZFPaSgW6/28yItmeJ0i+HpYbtOEGF6lJnEtI/jWNc0qb8/daE+HiahcKndpwi2zlErjlFfry08P3u5R7iX/KbGsgn96pVt+G9SXBRLX84ymWaqsg70xA+wgSov0xTc6AMHG15aHSrryw+RAikDbMU4ooNazDmeMWsitQNa8c120TPUQM/h+/ysNdksjnxDkyjOekzpyJmalGorfBe/KbRVqd2fK5VwIh4wJqWvPP2Gofh0C1sawQf2fu0KHHHg8XQhT+MivvrYzs0rccHnRiYcbDX3IPUGqoedaD3Q+Gkqo33XRqq+IJKlAFM=");
    private LobbyHologram hologram;

    private HashMap<String, Object> options = new HashMap<>();
    private HashMap<EquipmentSlot, Item> equipment = new HashMap<>();
    private List<String> clickActions = new ArrayList<>();

    public NPC toNPC() {

        hologram.setLocation(location.clone().add(0, 1, 0));

        Boolean lookAtPlayer = (Boolean) options.getOrDefault("look_at_player", false);
        Boolean glow = (Boolean) options.getOrDefault("glow", false);

        Consumer<NPCClickEvent> clickEventConsumer = (npcClickEvent) -> {

            Player player = npcClickEvent.getPlayer();

            ActionsProcessor.process(clickActions, player, Pair.of("click_action", npcClickEvent.getClickAction().name()));

            //If You need to check the click action:
            //Usage: [MESSAGE] ?EQUALS: %click_action% = RIGHT_CLICK? Your message here
        };

        switch (type) {
            case MOB:

                String entityType = options.getOrDefault("entity_type", "PIG").toString();

                return new MobNPC(
                        XG7Lobby.getInstance(),
                        "xg7lobby_npc-" + id,
                        hologram.toHologram(),
                        getPacketEventsEntityType(entityType),
                        location,
                        equipment,
                        lookAtPlayer,
                        glow,
                        clickEventConsumer
                );

            case PLAYER:

                System.out.println("TO NPC PLAYER ");
                return new PlayerNPC(
                        XG7Lobby.getInstance(),
                        "xg7lobby_npc-" + id,
                        skin,
                        hologram.toHologram(),
                        location,
                        equipment,
                        lookAtPlayer,
                        glow,
                        clickEventConsumer
                );

            case ITEM_DISPLAY:

                Item item = Item.from(Item.fromJson(options.get("item").toString()));

                List<Double> scale =  options.get("scale") != null ?  (List<Double>) options.get("scale") : Arrays.asList(1.0, 1.0, 1.0);
                float rotationX = options.get("rotationX") != null ? ((Number) options.get("rotationX")).floatValue()  : 0.0f;
                float rotationY = options.get("rotationY") != null ? ((Number) options.get("rotationY")).floatValue() : 0.0f;
                EntityDisplayOptions.Billboard billboard = options.get("billboard") != null ? EntityDisplayOptions.Billboard.valueOf(options.get("billboard").toString().toUpperCase()) : EntityDisplayOptions.Billboard.FIXED;

                System.out.println("SCALE: " + scale);

                return new DisplayNPC(
                        XG7Lobby.getInstance(),
                        "xg7lobby_npc-" + id,
                        hologram.toHologram(),
                        true,
                        item,
                        EntityDisplayOptions.ofItemDisplay(new Vector3f(scale.get(0).floatValue(), scale.get(2).floatValue(), scale.get(2).floatValue()), rotationX, rotationY, billboard),
                        location,
                        equipment,
                        lookAtPlayer,
                        glow,
                        clickEventConsumer
                );

        }

        return null;
    }

    public enum NPCType {
        MOB,
        PLAYER,
        ITEM_DISPLAY
    }

    public enum SkinType {
        URL,
        USERNAME,
        UUID
    }

    private static EntityType getPacketEventsEntityType(String content) {

        ReflectionClass entityTypesClass = ReflectionClass.of(EntityTypes.class);

        try {
            return entityTypesClass.getStaticField(content.toUpperCase());
        } catch (Exception ignored) {
            return EntityTypes.PIG;
        }
    }

}
