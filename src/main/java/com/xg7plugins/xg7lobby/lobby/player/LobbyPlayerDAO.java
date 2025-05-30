package com.xg7plugins.xg7lobby.lobby.player;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.data.dao.DAO;
import com.xg7plugins.data.database.query.Query;
import com.xg7plugins.data.database.query.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LobbyPlayerDAO implements DAO<UUID, LobbyPlayer> {
    @Override
    public CompletableFuture<Boolean> add(LobbyPlayer data) throws ExecutionException, InterruptedException {
        if(data == null || data.getID() == null) return CompletableFuture.completedFuture(null);

        return XG7PluginsAPI.dbProcessor().exists(
                XG7Plugins.getInstance(), LobbyPlayer.class, "player_id", data.getID()
        ).thenApply(exists -> {
            if (exists) return false;
            try {
                Transaction.createTransaction(
                        XG7Plugins.getInstance(),
                        data,
                        Transaction.Type.INSERT
                ).waitForResult();
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });


    }

    @Override
    public CompletableFuture<LobbyPlayer> get(UUID uuid) {
        if (uuid == null) return null;

        return XG7PluginsAPI.database().containsCachedEntity(XG7Plugins.getInstance(), uuid.toString()).thenComposeAsync(exists -> {
            if (exists) return XG7PluginsAPI.database().getCachedEntity(XG7Plugins.getInstance(), uuid.toString());

            try {
                return CompletableFuture.completedFuture(Query.selectFrom(XG7Plugins.getInstance(), LobbyPlayer.class, uuid).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult().get(LobbyPlayer.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } ,XG7PluginsAPI.taskManager().getAsyncExecutors().get("database"));


    }

    @Override
    public CompletableFuture<List<LobbyPlayer>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Query.selectAllFrom(getPlugin(), LobbyPlayer.class).waitForResult().getList(LobbyPlayer.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> update(LobbyPlayer data) {
        if (data == null) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction.update(XG7Plugins.getInstance(), data).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult();
                XG7PluginsAPI.database().cacheEntity(XG7Plugins.getInstance(), data.getID().toString(), data);
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }, XG7PluginsAPI.taskManager().getAsyncExecutors().get("database"));

    }

    @Override
    public CompletableFuture<Boolean> delete(LobbyPlayer data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Transaction.delete(XG7Plugins.getInstance(), data).onError(e -> {
                    throw new RuntimeException(e);
                }).waitForResult();
                XG7PluginsAPI.database().unCacheEntity(getPlugin(), data.getID().toString());
                return true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Plugin getPlugin() {
        return XG7Plugins.getInstance();
    }
}
