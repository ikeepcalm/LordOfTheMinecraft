package dev.ua.ikeepcalm.entities.mobs;


import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PassiveAbilities {

    public static void passiveAbility(String id, Entity entity) {
        if (id.equals("rooster") || id.equals("fire-salamander") || id.equals("magma-elf")) {
            execute(entity);
        }
    }

    private static void execute(Entity entity) {
        final HashMap<Long, Location> burnedLocations = new HashMap<>();
        Random random = new Random();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    burnedLocations.put(System.currentTimeMillis(), entity.getLocation());

                    for (Location loc : burnedLocations.values()) {
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FLAME, loc, 2, .05, .05, .05, 0);
                        if (loc.getWorld().getNearbyEntities(loc, 1, 1, 1).isEmpty())
                            continue;
                        for (Entity e : loc.getWorld().getNearbyEntities(loc, .5, .5, .5)) {
                            if (!(e instanceof Damageable damageable) || damageable == entity)
                                continue;
                            damageable.damage(6, entity);
                        }
                    }

                    try {
                        for (Long l : burnedLocations.keySet()) {
                            long temp = l;
                            if ((temp + (1000 * 5)) < System.currentTimeMillis())
                                burnedLocations.remove(l);
                        }
                    } catch (Exception ignored) {
                    }

                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Rooster");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);

    }
}
