package com.xg7plugins.xg7lobby.holograms.data;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.google.gson.internal.LinkedTreeMap;
import com.xg7plugins.modules.xg7holograms.hologram.line.HologramLine;
import com.xg7plugins.modules.xg7holograms.hologram.line.impl.*;
import com.xg7plugins.utils.EntityDisplayOptions;
import com.xg7plugins.utils.item.Item;
import com.xg7plugins.utils.reflection.ReflectionClass;
import lombok.Data;
import org.bukkit.enchantments.Enchantment;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyHologramLine {

    private String content;
    private Type type;
    private HashMap<EquipmentSlot, Item> equipment = new HashMap<>();

    private HashMap<String, Object> options = new HashMap<>();

    public LobbyHologramLine(String content) {
        this.content = content;
        this.type = Type.ARMOR_STAND;
    }

    private LobbyHologramLine() {}

    public HologramLine toHologramLine() {

        if (
                options.get("spacing") == null || !(options.get("spacing") instanceof Number) &&
                options.get("levitate") == null || !(options.get("levitate") instanceof Boolean)
        ) {
            options.put("spacing", 0.3f);
            options.put("levitate", false);
        }

        switch (type) {

            case INVISIBLE:
                return new InvisibleArmorStandLine(
                        ((Number) options.get("spacing")).floatValue(),
                        (Boolean) options.get("levitate"),
                        equipment
                );
            case ITEM:

                String name = options.get("name") != null ? (String) options.get("name") : "";
                List<String> lore = options.get("lore") != null ? (List<String>) options.get("lore") : Collections.emptyList();
                int amount = options.get("amount") != null ? (Integer) options.get("amount") : 1;
                LinkedTreeMap<String, Number> enchantments = options.get("enchantments") != null ? (LinkedTreeMap<String, Number>) options.get("enchantments") : new LinkedTreeMap<>();

                System.out.println("CONTENT: " + content);
                System.out.println(Item.from(content).getItemStack());

                return new ItemLine(
                        Item.from(content)
                                .name(name)
                                .lore(lore)
                                .amount(amount)
                                .enchants(enchantments
                                        .entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                entry -> Enchantment.getByName(entry.getKey()),
                                                entry -> entry.getValue().intValue()
                                        ))
                                ),
                        ((Number) options.get("spacing")).floatValue(),
                        (Boolean) options.get("levitate"),
                        equipment
                );

            case ENTITY:

                System.out.println("CONTENT: " + content);

                return new EntityLine(
                        getPacketEventsEntityType(content),
                        ((Number) options.get("spacing")).floatValue(),
                        (Boolean) options.get("levitate"),
                        equipment
                );

            case DISPLAY:

                List<Double> scale =  options.get("scale") != null ?  (List<Double>) options.get("scale") : Arrays.asList(1.0, 1.0, 1.0);
                float rotationX = options.get("rotationX") != null ? ((Number) options.get("rotationX")).floatValue()  : 0.0f;
                float rotationY = options.get("rotationY") != null ? ((Number) options.get("rotationY")).floatValue() : 0.0f;
                boolean background = options.get("background") != null ? (Boolean) options.get("background") : false;
                Color backgroundColor = options.get("background-color") != null ? Color.decode(options.get("background-color").toString()) : Color.BLACK;
                boolean shadow = options.get("shadow") != null ? (Boolean) options.get("shadow") : true;
                boolean seeThrough = options.get("see-through") != null ? (Boolean) options.get("see-through") : false;
                EntityDisplayOptions.Billboard billboard = options.get("billboard") != null ? EntityDisplayOptions.Billboard.valueOf(options.get("billboard").toString().toUpperCase()) : EntityDisplayOptions.Billboard.FIXED;
                EntityDisplayOptions.Alignment alignment = options.get("alignment") != null ? EntityDisplayOptions.Alignment.valueOf(options.get("alignment").toString().toUpperCase()) : EntityDisplayOptions.Alignment.CENTER;

                return new TextDisplayLine(
                        content,
                        ((Number) options.get("spacing")).floatValue(),
                        (Boolean) options.get("levitate"),
                        equipment,
                        EntityDisplayOptions.of(
                                new Vector3f(scale.get(0).floatValue(), scale.get(2).floatValue(), scale.get(2).floatValue()),
                                rotationX,
                                rotationY,
                                background,
                                backgroundColor,
                                shadow,
                                seeThrough,
                                billboard,
                                alignment
                        )
                );

            default:
                return new ArmorStandLine(
                        content,
                        ((Number) options.get("spacing")).floatValue(),
                        (Boolean) options.get("levitate"),
                        equipment
                );

        }
    }

    public enum Type {
        ARMOR_STAND,
        ITEM,
        DISPLAY,
        INVISIBLE,
        ENTITY;

        public static Type fromString(String str) {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return ARMOR_STAND;
        }
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
