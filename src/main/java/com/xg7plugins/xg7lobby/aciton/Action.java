package com.xg7plugins.xg7lobby.aciton;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import com.xg7plugins.utils.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class Action {

    private final ActionType actionType;
    private final String conditionLine;
    private final String[] args;

    public void execute(Player player) {
        if (conditionLine != null && Condition.processConditions(Text.format(conditionLine).textFor(player).getPlainText(), player).isEmpty()) return;
        actionType.execute(player, args);
    }
}
