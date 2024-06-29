package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Objects;

public class HolyLight extends Ability {

    public HolyLight(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        loc.add(0, 14, 0);

        final Material[] lastMaterial = {loc.getBlock().getType()};

        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                    for (double i = 0; i < 3.2; i += 0.8) {
                        for (int j = 0; j < 50; j++) {
                            double x = i * Math.cos(j);
                            double z = i * Math.sin(j);
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, dust);
                                loc.getWorld().spawnParticle(Particle.FIREWORK, loc.getX() + x, loc.getY() + 1, loc.getZ() + z, 1, 0, 0, 0, 0);
                            });
                        }
                    }
                });

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    loc.getBlock().setType(lastMaterial[0]);
                    loc.subtract(0, 0.75, 0);
                    lastMaterial[0] = loc.getBlock().getType();
                    loc.getBlock().setType(Material.LIGHT);

                    if ((lastMaterial[0].isSolid() && counter >= 18.6) || counter >= 200) {
                        loc.getBlock().setType(lastMaterial[0]);
                        counter = 0;
                        cancel();
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;

                        // Damage nearby entities
                        ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc.subtract(5, 0, 5), 10, 10, 10);
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof LivingEntity livingEntity) {
                                if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                                    ((Damageable) entity).damage(15 * multiplier, caster);
                                } else {
                                    if (entity != caster)
                                        ((Damageable) entity).damage(8 * multiplier, caster);
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        p = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(p, 15);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        Location loc = lastBlock.getLocation();

        executeAbility(loc, p, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.GLOWSTONE_DUST, "Світло", "45", identifier);
    }
}
