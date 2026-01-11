package com.xg7plugins.xg7lobby.queue;

import com.xg7plugins.xg7lobby.acitons.Action;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LobbyQueueEntry {

    private final UUID uuid;
    private final Action action;
    private final boolean hasPriority;
    private final boolean isOp;
    private final long timeStamp;

    public LobbyQueueEntry(UUID uuid, Action action, boolean hasPriority, boolean isOp) {
        this.uuid = uuid;
        this.action = action;
        this.hasPriority = hasPriority;
        this.isOp = isOp;
        this.timeStamp = System.nanoTime();
    }
}
