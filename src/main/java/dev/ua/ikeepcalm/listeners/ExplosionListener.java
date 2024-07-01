package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.Beyonder;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExplosionListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ExplosionListener.class);
    private final CoreProtectAPI coreProtectAPI = LordOfTheMinecraft.coreProtect;
    private final Map<UUID, Long> activeSeries = new HashMap<>();
    private final Map<UUID, Set<Location>> seriesBlocks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> scheduledTasks = new HashMap<>();
    private static final int RADIUS = 200;
    private static final long SERIES_TIMEOUT = 20 * 20L; // 20 seconds

    @EventHandler
    public void onMythicExplosion(BlockExplodeEvent event) {
        Collection<Player> players = event.getBlock().getWorld().getNearbyPlayers(event.getBlock().getLocation(), RADIUS, RADIUS, RADIUS);
        for (Player player : players) {
            if (LordOfTheMinecraft.beyonders.containsKey(player.getUniqueId())) {
                Beyonder beyonder = LordOfTheMinecraft.beyonders.get(player.getUniqueId());
                if (beyonder.online) {
                    event.setYield(0);
                    UUID seriesUUID = getOrCreateSeriesUUID(player);
                    List<Block> blocks = event.blockList();
                    Set<Location> loggedLocations = seriesBlocks.computeIfAbsent(seriesUUID, k -> new HashSet<>());
                    for (Block block : blocks) {
                        if (coreProtectAPI != null) {
                            Location location = block.getLocation();
                            if (loggedLocations.add(location)) {
                                coreProtectAPI.logRemoval(String.valueOf(seriesUUID), location, block.getType(), block.getBlockData());
                            }
                        }
                    }
                    activeSeries.put(seriesUUID, System.currentTimeMillis());
                    scheduleRollbackCheck(seriesUUID);
                }
            }
        }
    }

    private UUID getOrCreateSeriesUUID(Player player) {
        for (Map.Entry<UUID, Long> entry : activeSeries.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() < SERIES_TIMEOUT) {
                return entry.getKey();
            }
        }
        UUID newUUID = UUID.randomUUID();
        activeSeries.put(newUUID, System.currentTimeMillis());
        return newUUID;
    }

    private void scheduleRollbackCheck(UUID seriesUUID) {
        BukkitRunnable existingTask = scheduledTasks.get(seriesUUID);
        if (existingTask != null) {
            existingTask.cancel();
        }
        BukkitRunnable rollbackTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - activeSeries.getOrDefault(seriesUUID, 0L) >= SERIES_TIMEOUT) {
                    rollbackChanges(seriesUUID);
                    activeSeries.remove(seriesUUID);
                    seriesBlocks.remove(seriesUUID);
                    scheduledTasks.remove(seriesUUID);
                    System.out.println("Rollback completed for UUID: " + seriesUUID);
                }
            }
        };
        rollbackTask.runTaskLaterAsynchronously(LordOfTheMinecraft.instance, SERIES_TIMEOUT);
        scheduledTasks.put(seriesUUID, rollbackTask);
    }

    private void rollbackChanges(UUID seriesUUID) {
        new BukkitRunnable() {
            @Override
            public void run() {
                coreProtectAPI.performRollback(
                        30,
                        List.of(String.valueOf(seriesUUID)),
                        null,
                        null,
                        null,
                        null,
                        0,
                        null);
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);
    }
}