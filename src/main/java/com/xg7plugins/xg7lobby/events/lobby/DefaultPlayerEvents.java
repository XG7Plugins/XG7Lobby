package com.xg7plugins.xg7lobby.events.lobby;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.xg7plugins.XG7PluginsAPI;
import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.section.ConfigBoolean;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.bukkitevents.EventHandler;
import com.xg7plugins.modules.xg7menus.menus.MenuAction;
import com.xg7plugins.server.MinecraftVersion;
import com.xg7plugins.tasks.tasks.BukkitTask;
import com.xg7plugins.utils.reflection.ReflectionObject;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.XG7LobbyAPI;
import com.xg7plugins.xg7lobby.configs.MainConfigs;
import com.xg7plugins.xg7lobby.configs.PVPConfigs;
import com.xg7plugins.xg7lobby.configs.PlayerConfigs;
import com.xg7plugins.xg7lobby.data.location.LobbyLocation;
import com.xg7plugins.xg7lobby.data.player.LobbyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.concurrent.CompletableFuture;

public class DefaultPlayerEvents implements Listener {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean (
                    configName = "config",
                    path = "break-blocks",
                    invert = true
            ),
            priority = EventPriority.HIGH
    )
    public void onBreakBlock(BlockBreakEvent event) {
        handleItemEvents(event, "player-prohibitions.break-blocks");
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean (
                    configName = "config",
                    path = "place-blocks",
                    invert = true
            ),
            priority = EventPriority.HIGH
    )
    public void onPlaceBlock(BlockPlaceEvent event) {
        handleItemEvents(event, "player-prohibitions.place-blocks");
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean (
                    configName = "config",
                    path = "interact-with-blocks",
                    invert = true
            ),
            priority = EventPriority.HIGH
    )
    public void onInteract(PlayerInteractEvent event) {
        MenuAction action = MenuAction.from(event.getAction());
        if (action.isBlockInteract() && action.isLeftClick()) {
            handleItemEvents(event, "player-prohibitions.break-blocks");
            return;
        }
        if (!(action.isRightClick() && action.isBlockInteract())) return;
        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;
        handleItemEvents(event, "player-prohibitions.interact-with-blocks");
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean (
                    configName = "config",
                    path = "drop-items",
                    invert = true
            ),
            priority = EventPriority.HIGH
    )
    public void onDropItem(PlayerDropItemEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;
        handleItemEvents(event,"player-prohibitions.drop-items");
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean (
                    configName = "config",
                    path = "pickup-items",
                    invert = true
            ),
            priority = EventPriority.HIGH
    )
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;
        handleItemEvents(event,"player-prohibitions.pickup-items");
    }

    private <T extends Event & Cancellable> void handleItemEvents(T event, String path) {

        Player player = ReflectionObject.of(event).getMethod("getPlayer").invoke();

        if (player.hasPermission("xg7lobby.build")) {
            LobbyPlayer lobbyPlayer = XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).join();
            if (lobbyPlayer.isBuildEnabled() || !Config.mainConfigOf(XG7Lobby.getInstance()).get("build-system-enabled", Boolean.class).orElse(false))  {
                event.setCancelled(false);
                return;
            }
            Text.sendTextFromLang(player, XG7Lobby.getInstance(), "build-not-enabled");
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        Text.sendTextFromLang(player,XG7Lobby.getInstance(), path);

    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(
                    configName = "config",
                    path = "take-damage",
                    invert = true
            )
    )
    public void onTakeDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(
                    configName = "config",
                    path = "attack",
                    invert = true
            )
    )
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (damager.hasPermission("xg7lobby.attack")) return;
        if (XG7LobbyAPI.isPlayerInPVP(victim) && XG7LobbyAPI.isPlayerInPVP(damager)) return;

        if (XG7LobbyAPI.isPlayerInPVP(damager) && !XG7LobbyAPI.isPlayerInPVP(victim)) {
            event.setCancelled(true);
            Text.sendTextFromLang(damager, XG7Lobby.getInstance(), "pvp.on-attack-in-pvp");
            return;
        }
        if (!XG7LobbyAPI.isPlayerInPVP(damager) && damager.hasPermission("xg7lobby.pvp")) {
            event.setCancelled(true);
            Text.sendTextFromLang(damager, XG7Lobby.getInstance(), "pvp.on-attack-out-pvp");
            return;
        }

        event.setCancelled(true);
        Text.sendTextFromLang(damager, XG7Lobby.getInstance(), "player-prohibitions.attack");
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(
                    configName = "config",
                    path = "food-change",
                    invert = true
            )
    )
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP((Player) event.getEntity()) && Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isFoodChange()) return;
        event.setCancelled(true);
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(
                    configName = "config",
                    path = "cancel-death-by-void"
            )
    )
    public void voidCheck(PlayerMoveEvent event) {

        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;

        User user = PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer());

        int layer = user.getClientVersion().isNewerThan(ClientVersion.V_1_17) && MinecraftVersion.isNewerOrEqual(17) ? -66 : -2;

        if (event.getPlayer().getLocation().getY() < layer) {
            LobbyLocation location = XG7LobbyAPI.requestRandomLobbyLocation().join();
            if (location == null || location.getLocation() == null) {
                XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation())));
                return;
            }

            XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> location.teleport(event.getPlayer())));
        }
    }

    @EventHandler(
            isOnlyInWorld = true,
            isEnabled = @ConfigBoolean(
                    configName = "config",
                    path = "cancel-portal-teleport"
            ),
            priority = EventPriority.LOW
    )
    public void onPortal(PlayerTeleportEvent event) {
        if (event.getTo() == null) return;
        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(!event.getFrom().getWorld().getEnvironment().equals(event.getTo().getWorld().getEnvironment()));
    }

    @EventHandler(isOnlyInWorld = true)
    public void onDeath(PlayerDeathEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP(event.getEntity()) && Config.of(XG7Lobby.getInstance(), PVPConfigs.class).isDropsEnabled()) return;
        event.getDrops().clear();
    }

    @EventHandler(isOnlyInWorld = true, priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP((Player) event.getWhoClicked())) return;
        if (!event.getWhoClicked().hasPermission("xg7lobby.inv")) event.setCancelled(true);
    }

    @EventHandler(isOnlyInWorld = true)
    public void onRespawn(PlayerRespawnEvent event) {
        if (XG7LobbyAPI.isPlayerInPVP(event.getPlayer())) return;

        Player player = event.getPlayer();

        CompletableFuture<LobbyLocation> lobbyLocation = XG7LobbyAPI.requestRandomLobbyLocation();

        lobbyLocation.thenAccept(lobby -> {
            if (lobby.getLocation() == null) {
                Text.sendTextFromLang(player, XG7Lobby.getInstance(), "lobby.on-teleport.on-error-doesnt-exist" + (player.hasPermission("xg7lobby.command.lobby.set") ? "-adm" : ""));
                return;
            }
            XG7PluginsAPI.taskManager().runSync(BukkitTask.of(XG7Lobby.getInstance(), () -> lobby.teleport(player)));
        });

        XG7LobbyAPI.customInventoryManager().openMenu(Config.of(XG7Lobby.getInstance(), MainConfigs.class).getMainSelectorId(), player);

        PlayerConfigs configs = Config.of(XG7Lobby.getInstance(), PlayerConfigs.class);

        configs.apply(player);

        player.setHealth(player.getMaxHealth());

        XG7LobbyAPI.requestLobbyPlayer(player.getUniqueId()).thenAccept(lobbyPlayer -> {
            lobbyPlayer.fly();
            lobbyPlayer.applyBuild();
            lobbyPlayer.applyHide();
        });

    }

}
