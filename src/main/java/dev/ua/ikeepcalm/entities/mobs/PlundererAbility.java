package dev.ua.ikeepcalm.entities.mobs;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.MobAbility;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlundererAbility extends MobAbility {
    public PlundererAbility(int frequency) {
        super(frequency);
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void useAbility(Location startLoc, Location endLoc, double multiplier, Entity user, Entity target) {
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(154, 0, 194), 1.25f);
        if (!(target instanceof Player playerTarget))
            return;

        Vector vector = (target.getLocation().toVector().subtract(startLoc.toVector())).normalize();
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    user.getWorld().spawnParticle(Particle.DUST, startLoc, 4, .05, .05, .05, dust);
                    startLoc.add(vector);


                    for (Entity e : user.getWorld().getNearbyEntities(startLoc, 1, 1, 1)) {
                        if (e != playerTarget)
                            continue;
                        playerTarget.damage(4, user);
                        playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                        playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2));
                        cancel();
                    }

                    counter++;
                    if (counter > 50)
                        cancel();
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Plunderer");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 4);
    }
}
