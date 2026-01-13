package com.xg7plugins.xg7lobby.tasks;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.tasks.TaskState;
import com.xg7plugins.tasks.tasks.TimerTask;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.queue.LobbyQueueEntry;
import com.xg7plugins.xg7lobby.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueTask extends TimerTask {

    private final QueueManager queueManager;

    public QueueTask(QueueManager queueManager) {
        super(
                XG7Lobby.getInstance(),
                "queues-task",
                0,
                ConfigFile.mainConfigOf(XG7Lobby.getInstance()).section("queue-system").getTimeInMilliseconds("delay"),
                TaskState.RUNNING,
                false
        );
        this.queueManager = queueManager;
    }

    @Override
    public void run() {
        queueManager.getQueues().entrySet().forEach(mapEntry -> {
            LobbyQueueEntry entry = mapEntry.getValue().poll();

            if (entry == null) return;
            Player player = Bukkit.getPlayer(entry.getUuid());
            if (player == null || player.isDead()) return;

            XG7Lobby.getInstance().getDebug().info("queue", "Polling " + player.getName() + " from queue " + mapEntry.getKey() + " and executing his action...");

            entry.getAction().execute(player);

            mapEntry.getValue().forEach(queueEntry -> {
                if (queueEntry == null) return;
                Player other = Bukkit.getPlayer(queueEntry.getUuid());
                if (other == null) return;
                Text.sendTextFromLang(other,  XG7Lobby.getInstance(), "queue.position", Pair.of("id", mapEntry.getKey()), Pair.of("pos", String.valueOf(queueManager.getPositionInQueue(mapEntry.getKey(), other))), Pair.of("max", String.valueOf(mapEntry.getValue().size())));
            });
        });

    }
}
