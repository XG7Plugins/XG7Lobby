package com.xg7plugins.xg7lobby.acitons;

import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionsProcessor {

    private static final Pattern conditionPattern = Pattern.compile("\\((.*?)\\) ");

    @SafeVarargs
    public static void process(List<String> actions, Player player, Pair<String, String>... placeholders) {
        process(actions, player, Arrays.asList(placeholders));
    }

    public static void process(List<String> actions, Player player, List<Pair<String, String>> placeholders) {
        actions.forEach(action -> {
            try {
                Debug.of(XG7Lobby.getInstance()).info("actions", "Executing actions for " + player.getName() + ". Actions: " + actions);
                getActionOf(action, placeholders).execute(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Action getActionOf(String line, List<Pair<String, String>> placeholders) {
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Pair<String, String> placeholder : placeholders) {
                line = line.replace("%" + placeholder.getFirst() + "%", placeholder.getSecond());
            }
        }
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
