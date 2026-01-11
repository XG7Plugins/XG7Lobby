package com.xg7plugins.xg7lobby.queue;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
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
                    if (a.isOp() != b.isOp()) return Boolean.compare(a.isOp(), b.isOp());
                    if (a.isHasPriority() != b.isHasPriority()) return Boolean.compare(a.isHasPriority(), b.isHasPriority());
                    return Long.compare(a.getTimeStamp(),  b.getTimeStamp());
                }
        )));

    }

    public PriorityQueue<LobbyQueueEntry> getQueue(String queueId) {
        return queues.get(queueId);
    }

    public void addToQueue(String queueID, Player player, Action action) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        queue.add(new LobbyQueueEntry(player.getUniqueId(), action, player.hasPermission("xg7lobby.queue.priority"), player.isOp()));
    }

    public void removeFromQueue(String queueID, Player player) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        queue.removeIf(entry -> entry.getUuid().equals(player.getUniqueId()));
    }

    public void removeFromAllQueues(Player player) {
        queues.forEach((key, value) -> value.removeIf(entry -> entry.getUuid().equals(player.getUniqueId())));
    }

    public boolean isInQueue(String queueID, Player player) {
        PriorityQueue<LobbyQueueEntry> queue = queues.get(queueID);
        if (queue == null) throw new RuntimeException("Queue is null");

        return queue.stream().anyMatch(entry -> entry.getUuid().equals(player.getUniqueId()));
    }

    public boolean exists(String queueID) {
        return queues.containsKey(queueID);
    }

    public void clear() {
        queues.clear();
    }



}
