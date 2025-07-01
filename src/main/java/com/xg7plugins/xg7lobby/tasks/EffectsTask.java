package com.xg7plugins.xg7lobby.tasks;

import com.cryptomorin.xseries.XPotion;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.tasks.tasks.Task;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Parser;
import com.xg7plugins.utils.time.Time;
import com.xg7plugins.xg7lobby.XG7Lobby;
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
                Config.mainConfigOf(XG7Lobby.getInstance()).getTime("effects-task-delay").orElse(Time.of(10)).getMilliseconds(),
                TaskState.RUNNING,
                false
        );

        Config config = Config.mainConfigOf(XG7Lobby.getInstance());

        List<String> effectsStringList = config.getList("effects", String.class).orElse(null);

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
//                    if (XG7Lobby.getInstance().getGlobalPVPManager().isPlayerInPVP(player)) return;
                    effects.forEach(effect -> {
                        player.removePotionEffect(effect.getType());
                        player.addPotionEffect(effect);
                    });
                });
    }
}
