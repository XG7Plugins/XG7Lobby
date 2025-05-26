package com.xg7plugins.xg7lobby.aciton;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionsProcessor {

    public void process(List<String> actions, Player player) {
        actions.forEach(action -> {
            try {
                getActionOf(action).execute(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Action getActionOf(String line) {
        String action = line.split(" ")[0];
        line = line.substring(action.length() + 1);

        ActionType type = ActionType.extractType(action);

        if (type == null) throw new ActionException(action, line);

        Pair<Condition,String> condition = Condition.extractCondition(line);

        if (condition != null) line = line.split("] ")[1];
        String[] args = line.split(", ");
        return new Action(type, condition, type.isNeedArgs() ? args : new String[]{line});
    }

}
