package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MeteorShower extends Ability {

    public MeteorShower(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 85; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);

        if (loc.getWorld() == null)
            return;

        Random random = new Random();

        for (int i = 0; i < random.nextInt(40, 80); i++) {
            Location starLoc = (i == 0) ? loc.clone() : loc.clone().add(random.nextInt(-45, 45), 0, random.nextInt(-45, 45));

            float angle = p.getEyeLocation().getYaw() / 60;
            Location startLoc = starLoc.clone().add(-Math.cos(angle) * 15, random.nextInt(40, 80), -Math.sin(angle) * 15);

            Vector fallDir = starLoc.clone().toVector().subtract(startLoc.clone().toVector()).normalize().multiply(2);

            if (starLoc.getWorld() == null || startLoc.getWorld() == null)
                return;

            // Run the meteor falling task asynchronously
            new BukkitRunnable() {
                @Override
                public void run() {
                    BukkitRunnable mainThreadTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Entity entity : startLoc.getWorld().getNearbyEntities(starLoc, 150, 150, 150)) {
                                if (!(entity instanceof Player player))
                                    continue;
                                player.spawnParticle(Particle.LAVA, startLoc, 2, 0, 0, 0, 0);
                                player.spawnParticle(Particle.SMOKE, startLoc, 2, 0, 0, 0, 0);
                            }

                            startLoc.add(fallDir);

                            if (startLoc.getBlock().getType().isSolid()) {
                                handleImpact(startLoc);
                                cancel();
                            }
                        }
                    };

                    // Run the main thread task synchronously
                    mainThreadTask.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
                }
            }.runTaskLater(LordOfTheMinecraft.instance, random.nextInt(20 * 4));
        }
    }

    private void handleImpact(Location impactLocation) {
        for (Entity entity : impactLocation.getWorld().getNearbyEntities(impactLocation, 10, 10, 10)) {
            if (entity == p)
                continue;
            if (entity instanceof Damageable d)
                d.damage(50, p);
        }

        impactLocation.getWorld().createExplosion(impactLocation, 25, false, false);
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.FIRE_CHARGE, "Метеоритна Злива", "60000", identifier);
    }
}
