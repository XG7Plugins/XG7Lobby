package com.xg7plugins.xg7lobby.aciton;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class Action {

    private final ActionType actionType;
    private Pair<Condition, String> condition;
    private final String[] args;

    public void execute(Player player) {
        if (condition != null && !condition.getFirst().apply(new Condition.ConditionPack(player,condition.getSecond()))) return;
        actionType.execute(player, args);
    }
}
