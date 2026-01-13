package com.xg7plugins.xg7lobby.queue;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.Action;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

public class QueueManager {

    @Getter
    private final HashMap<String, PriorityQueue<LobbyQueueEntry>> queues = new HashMap<>();

    public void init() {
        ConfigSection queueSection = ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("queue-system");
        queueSection.getList("queues", String.class).orElse(new ArrayList<>()).forEach(queue -> queues.put(queue, new PriorityQueue<>(
                (a, b) -> {
                    if (a.isOp() != b.isOp()) return Boolean.compare(b.isOp(), a.isOp());
                    if (a.isHasPriority() != b.isHasPriority()) return Boolean.compare(b.isHasPriority(), a.isHasPriority());
                    return Long.compare(a.getTimeStamp(), b.getTimeStamp());
                }
        )));
    }

    public PriorityQueue<LobbyQueueEntry> getQueue(String queueId) {
        return queues.get(queueId);
    }

    public void addToQueue(String queueID, Player player, Action action) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        if (ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("queue-system").get("one-queue-per-player")) removeFromAllQueues(player);

        queue.add(new LobbyQueueEntry(player.getUniqueId(), action, player.hasPermission("xg7lobby.queue.priority"), player.isOp()));

        Text.sendTextFromLang(player,  XG7Lobby.getInstance(), "queue.on-add", Pair.of("id", queueID));
        Text.sendTextFromLang(player,  XG7Lobby.getInstance(), "queue.position", Pair.of("id", queueID), Pair.of("pos", String.valueOf(getPositionInQueue(queueID, player))), Pair.of("max", String.valueOf(queue.size())));

    }

    public void removeFromQueue(String queueID, Player player) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        queue.removeIf(entry -> {
            boolean remove = entry.getUuid().equals(player.getUniqueId());
            if (remove) Text.sendTextFromLang(player,  XG7Lobby.getInstance(), "queue.removed", Pair.of("id", queueID));
            return remove;
        });
    }

    public void removeFromAllQueues(Player player) {
        queues.forEach((key, value) -> value.removeIf(entry -> {
            boolean remove = entry.getUuid().equals(player.getUniqueId());
            if (remove) Text.sendTextFromLang(player,  XG7Lobby.getInstance(), "queue.removed", Pair.of("id", key));
            return remove;
        }));
    }

    public boolean isInQueue(String queueID, Player player) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        return queue.stream().anyMatch(entry -> entry.getUuid().equals(player.getUniqueId()));
    }

    public boolean exists(String queueID) {
        return queues.containsKey(queueID);
    }

    public int getPositionInQueue(String queueID, Player player) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        List<LobbyQueueEntry> list = new ArrayList<>(queue);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUuid().equals(player.getUniqueId())) {
                return i + 1;
            }
        }
        return -1;
    }

    public void clear() {
        queues.clear();
    }



}
