package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class BeamOfLight extends Ability {

    private static final Logger log = LoggerFactory.getLogger(BeamOfLight.class);

    public BeamOfLight(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        double multiplier = getMultiplier();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        Location loc = p.getEyeLocation();
        Vector direction = loc.getDirection().normalize().multiply(.5);
        World world = p.getWorld();

        Random random = new Random();

        // First Runnable: Particle Beam
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                Location tempLoc = loc.clone();
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);

                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                    for (int i = 0; i < 48; i++) {
                        tempLoc.add(direction);
                        world.spawnParticle(Particle.DUST, tempLoc, 2, 0, 0, 0, dust);
                    }
                });

                if (counter > 25) cancel();
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);

        // Second Runnable: Fire Creation and Damage
        new BukkitRunnable() {
            final int circlePoints = 8;
            double radius = .10;
            final Location loc = p.getEyeLocation();
            final World world = p.getWorld();
            final double pitch = (loc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -loc.getYaw() * 0.017453292F;
            final double increment = (2 * Math.PI) / circlePoints;
            UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                    Location tempLoc = loc.clone();
                    for (int i = 0; i < 48; i++) {
                        tempLoc.add(direction);

                        // Particle effects and block interactions
                        for (int j = 0; j < circlePoints; j++) {
                            double angle = j * increment;
                            double x = radius * Math.cos(angle);
                            double z = radius * Math.sin(angle);

                            Vector vec = new Vector(x, 0, z);
                            MathVectorUtils.rotateAroundAxisX(vec, pitch);
                            MathVectorUtils.rotateAroundAxisY(vec, yaw);
                            tempLoc.add(vec);

                            world.spawnParticle(Particle.END_ROD, tempLoc, 1, .05, .05, .05, 0);
                            if (tempLoc.getBlock().getType().getHardness() > 0) {
                                if (random.nextInt(3) == 0) {
                                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                        logBlockBreak(uuid, new CustomLocation(tempLoc));
                                        tempLoc.getBlock().setType(Material.FIRE);
                                    });
                                } else {
                                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                        logBlockBreak(uuid, new CustomLocation(tempLoc));
                                        tempLoc.getBlock().setType(Material.AIR);
                                    });
                                }
                            }

                            tempLoc.subtract(vec);
                        }

                        // Entity interactions
                        for (Entity e : world.getNearbyEntities(tempLoc, 4, 4, 4)) {
                            if (!(e instanceof LivingEntity livingEntity) || e == p) continue;
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(e.getType())) livingEntity.damage(18 * multiplier);
                                livingEntity.damage(10 * multiplier);
                            });
                        }
                    }

                    radius += .25;

                    if (radius > 1.75) {
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> rollbackChanges(uuid));
                        cancel();
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    }
                });
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 25, 0);

        // Third Runnable: Additional Particle Effects
        new BukkitRunnable() {
            final int circlePoints = 20;
            double radius = .15;
            final Location loc = p.getEyeLocation();
            final World world = p.getWorld();
            final double pitch = (loc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -loc.getYaw() * 0.017453292F;
            final double increment = (2 * Math.PI) / circlePoints;

            @Override
            public void run() {
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                Location tempLoc = loc.clone();

                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                    for (int i = 0; i < 48; i++) {
                        tempLoc.add(direction);

                        // Particle effects
                        for (int j = 0; j < circlePoints; j++) {
                            double angle = j * increment;
                            double x = radius * Math.cos(angle);
                            double z = radius * Math.sin(angle);

                            Vector vec = new Vector(x, 0, z);
                            MathVectorUtils.rotateAroundAxisX(vec, pitch);
                            MathVectorUtils.rotateAroundAxisY(vec, yaw);
                            tempLoc.add(vec);

                            world.spawnParticle(Particle.DUST, tempLoc, 5, .15, .15, .15, dust);

                            tempLoc.subtract(vec);
                        }
                    }

                    radius += .6;
                    if (radius > 2.5) cancel();
                });
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 24, 0);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.GOLDEN_HOE, "Світловий Промінь", "2000", identifier);
    }
}
