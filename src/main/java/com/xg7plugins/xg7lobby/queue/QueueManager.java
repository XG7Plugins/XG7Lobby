package com.xg7plugins.xg7lobby.queue;

import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.acitons.Action;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.HashMap;
import java.util.UUID;

public class QueueManager {

    private final HashMap<String, Deque<Pair<UUID, Action>>> queues = new HashMap<>();

    public void init() {

    }

    public Deque<Pair<UUID, Action>> getQueue(String queueId) {
        return queues.get(queueId);
    }

    public void addToQueue(String queueID, Player player, Action action) {

        Deque<Pair<UUID, Action>> queue = queues.get(queueID);

        if (queue == null) {
            throw new RuntimeException("Queue is null");
        }

        boolean hasPriority = player.hasPermission("xg7lobby.queue.priority");

        if (queue.stream().anyMatch(pair -> pair.getFirst().equals(player.getUniqueId()))) return;

        if (hasPriority) {
            queue.add(Pair.of(player.getUniqueId(), action));
        }

        queue.addLast(Pair.of(player.getUniqueId(), action));

    }

    public boolean isInQueue(String queueID, Player player) {
        Deque<Pair<UUID, Action>> queue = queues.get(queueID);

        if (queue == null) {
            throw new RuntimeException("Queue is null");
        }

        return queue.stream().anyMatch(pair -> pair.getFirst().equals(player.getUniqueId()));
    }



}
