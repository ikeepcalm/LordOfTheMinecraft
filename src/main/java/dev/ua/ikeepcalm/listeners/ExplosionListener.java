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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ExplosionListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ExplosionListener.class);
    private final CoreProtectAPI coreProtectAPI = LordOfTheMinecraft.coreProtect;

    @EventHandler
    public void onMythicExplosion(BlockExplodeEvent event) {
        log.info("Block exploded");
        Collection<Player> entities = event.getBlock().getWorld().getNearbyPlayers(event.getBlock().getLocation(), 500);
        for (Player player : entities) {
            if (LordOfTheMinecraft.beyonders.containsKey(player.getUniqueId())) {
                Beyonder beyonder = LordOfTheMinecraft.beyonders.get(player.getUniqueId());
                log.info("Beyonder: " + beyonder);
                if (beyonder.online) {
                    log.info("Beyonder is online");
                    event.setYield(0);
                    UUID uuid = UUID.randomUUID();
                    List<Block> blocks = event.blockList();
                    for (Block block : blocks) {
                        if (coreProtectAPI != null) {
                            Location location = block.getLocation();
                            coreProtectAPI.logRemoval(String.valueOf(uuid), location, block.getType(), block.getBlockData());
                        }
                    }
                    rollbackChanges(uuid);
                }
            }
        }
    }

    private void rollbackChanges(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                coreProtectAPI.performRollback(
                        30,
                        List.of(String.valueOf(uuid)),
                        null,
                        null,
                        null,
                        null,
                        0,
                        null);
            }
        }.runTaskLaterAsynchronously(LordOfTheMinecraft.instance, 20 * 20L);
    }

}
