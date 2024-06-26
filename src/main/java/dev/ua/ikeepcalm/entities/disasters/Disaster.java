package dev.ua.ikeepcalm.entities.disasters;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;


public abstract class Disaster {

    final CoreProtectAPI coreProtectAPI = LordOfTheMinecraft.coreProtect;

    public Disaster() {
    }

    public abstract void spawnDisaster(LivingEntity e, Location loc);

    public abstract ItemStack getItem();

    public void logBlockBreak(UUID uuid, CustomLocation location) {
        if (coreProtectAPI != null) {
            coreProtectAPI.logRemoval(String.valueOf(uuid), location.toLocation(), location.type(), location.data());
        }
    }

    // Method to rollback changes
    protected void rollbackChanges(UUID uuid) {
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
