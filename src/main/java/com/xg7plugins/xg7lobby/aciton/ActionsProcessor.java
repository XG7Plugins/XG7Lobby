package com.xg7plugins.xg7lobby.aciton;

import com.xg7plugins.managers.Manager;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Condition;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionsProcessor {

    private static final Pattern conditionPattern = Pattern.compile("\\((.*?)\\)");

    public static void process(List<String> actions, Player player) {
        actions.forEach(action -> {
            try {
                getActionOf(action).execute(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Action getActionOf(String line) {
        String action = line.split(" ")[0];
        line = line.substring(action.length() + 1);

        ActionType type = ActionType.extractType(action);

        if (type == null) throw new ActionException(action, line);

        Matcher matcher = conditionPattern.matcher(line);

        String conditionLine = null;
        String allConditionLine = null;

        if (matcher.find()) {
            conditionLine = matcher.group() + " Random_content";
            allConditionLine = matcher.group(0);
        }

        if (allConditionLine != null) line = line.replaceFirst(Pattern.quote(allConditionLine), "");

        String[] args = line.split(", ");
        return new Action(type, conditionLine, type.isNeedArgs() ? args : new String[]{line});
    }

}
