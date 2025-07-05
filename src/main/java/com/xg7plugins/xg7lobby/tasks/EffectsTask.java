package com.xg7plugins.xg7lobby.tasks;

import com.cryptomorin.xseries.XPotion;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.AdvertisementConfigs;
import com.xg7plugins.xg7lobby.configs.MainConfigs;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectsTask extends TimerTask {
    private final List<PotionEffect> effects = new ArrayList<>();

    public EffectsTask() {
        super(
                XG7Lobby.getInstance(),
                "effects",
                0,
                Config.of(XG7Lobby.getInstance(), MainConfigs.class).getEffectsTaskDelay().getMilliseconds(),
                TaskState.RUNNING,
                false
        );

        MainConfigs config = Config.of(XG7Lobby.getInstance(), MainConfigs.class);

        List<String> effectsStringList = config.getEffects();

        if (effectsStringList == null) return;

        effectsStringList.forEach(effectString -> {

            String[] effect = effectString.split(", ");

            XPotion potion = XPotion.matchXPotion(effect[0]).orElse(null);

            if (potion == null) return;

            PotionEffect potionEffect = new PotionEffect(potion.getPotionEffectType(), 600, Parser.INTEGER.convert(effect[1]), false, true);

            effects.add(potionEffect);
        });

    }

    @Override
    public void run() {
        if (effects.isEmpty()) return;
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> XG7PluginsAPI.isInAnEnabledWorld(XG7Lobby.getInstance(), p)).
                forEach(player -> {

                    if (Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isDisableLobbyEffects() && XG7LobbyAPI.isPlayerInPVP(player)) return;

                    effects.forEach(effect -> {
                        player.removePotionEffect(effect.getType());
                        player.addPotionEffect(effect);
                    });
                });
    }
}
