package dev.ua.ikeepcalm.entities.mobs;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.MobAbility;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BaneAbility extends MobAbility {

    public BaneAbility(int frequency) {
        super(frequency);
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void useAbility(Location startLoc, Location endLoc, double multiplier, Entity user, Entity target) {
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(200, 60, 0), 2f);
        Location loc = startLoc.clone();
        Vector vector = (endLoc.clone().toVector().subtract(loc.toVector())).normalize();
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    user.getWorld().spawnParticle(Particle.DUST, loc, 45, .5, .5, .5, dust);
                    loc.add(vector);

                    for (Entity e : user.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                        if (e instanceof LivingEntity livingEntity)
                            livingEntity.damage(12, user);
                    }

                    counter++;
                    if (counter > 50)
                        cancel();
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Bane");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }
}
