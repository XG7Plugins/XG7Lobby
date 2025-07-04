package com.xg7plugins.xg7lobby.pvp;

import org.bukkit.event.entity.EntityDamageEvent;

public enum DeathCause {

    FALL,
    VOID,
    FIRE,
    LAVA,
    DROWNING,
    EXPLOSION,
    PROJECTILE,
    MELEE,
    MAGIC,
    POISON,
    STARVATION,
    SUFFOCATION,
    LIGHTNING,
    CACTUS,
    FREEZING,
    THORNS,
    ANVIL,
    BLOCK_CRUSHING,
    END_CRYSTAL,
    FIRE_TICK,
    SWEET_BERRY_BUSH,
    DRAGON_BREATH,
    WITHER,
    FALL_OUT_OF_WORLD,
    GENERIC;

    String getLangPath() {
        return "lang:[pvp.causes." + this.name().toLowerCase() + "]";
    }

    public static DeathCause fromCause(EntityDamageEvent.DamageCause damageCause) {
        try {
            return DeathCause.valueOf(damageCause.name());
        } catch (IllegalArgumentException e) {
            switch (damageCause) {
                case CONTACT:
                    return CACTUS;
                case ENTITY_ATTACK:
                case ENTITY_SWEEP_ATTACK:
                    return MELEE;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    return EXPLOSION;
                case MELTING:
                    return FIRE;
                case FALLING_BLOCK:
                    return ANVIL;
                case HOT_FLOOR:
                    return SWEET_BERRY_BUSH;
                case CRAMMING:
                    return BLOCK_CRUSHING;
                case DRYOUT:
                    return FREEZING;
                default:
                    return GENERIC;
            }
        }
    }

}
