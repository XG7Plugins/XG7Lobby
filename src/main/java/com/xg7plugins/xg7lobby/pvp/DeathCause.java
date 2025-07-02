package com.xg7plugins.xg7lobby.pvp;

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

}
