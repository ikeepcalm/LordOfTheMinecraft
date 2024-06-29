package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
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
import java.util.Random;
import java.util.UUID;

public class LightOfHoliness extends Ability {

    public LightOfHoliness(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        loc.add(0, 19, 0);

        final Material[] lastMaterial = {loc.getBlock().getType()};
        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            int counter = 0;
            final UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                counter++;

                scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                    // Particles
                    Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                    spawnParticles(loc, dust);
                });

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    // Light at current loc
                    loc.getBlock().setType(lastMaterial[0]);
                    loc.subtract(0, 1, 0);
                    lastMaterial[0] = loc.getBlock().getType();
                    loc.getBlock().setType(Material.LIGHT);

                    // Reached ground
                    if ((lastMaterial[0].isSolid() && counter >= 17) || counter >= 200) {
                        loc.getBlock().setType(lastMaterial[0]);
                        counter = 0;
                        cancel();

                        // Light that stays at the ground for a bit
                        lightGround(loc);

                        // Replace the ground with burned stuff
                        burnGround(loc, caster, uuid);

                        // Damage nearby entities
                        damageNearbyEntities(loc, caster, multiplier);

                        // Particles on ground
                        spawnGroundParticles(loc);
                    }
                });
            }

            @Override
            public void cancel() {
                super.cancel();
                scheduler.runTask(LordOfTheMinecraft.instance, () -> rollbackChanges(uuid));
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private void spawnParticles(Location loc, Particle.DustOptions dust) {
        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc.getX() + 3.8, loc.getY(), loc.getZ(), 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX() - 3.8, loc.getY(), loc.getZ(), 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX(), loc.getY(), loc.getZ() + 3.8, 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX(), loc.getY(), loc.getZ() - 3.8, 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX() + 3, loc.getY(), loc.getZ() + 3, 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX() - 3, loc.getY(), loc.getZ() - 3, 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX() - 3, loc.getY(), loc.getZ() + 3, 15, 0.15, 0, 0.15, 0, dust);
        loc.getWorld().spawnParticle(Particle.DUST, loc.getX() + 3, loc.getY(), loc.getZ() - 3, 15, 0.15, 0, 0.15, 0, dust);

        for (double i = 0; i < 3.8; i += 0.4) {
            for (int j = 0; j < 40; j++) {
                double x = i * Math.cos(j);
                double z = i * Math.sin(j);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, 0.1, 0.2, 0.1, 0);
                if ((int) loc.getY() % 2 == 0)
                    loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + x, loc.getY() + 0.25, loc.getZ() + z, 1, 0.2, 0, 0.2, 0);
            }
        }
    }

    private void lightGround(Location loc) {
        Location lightLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
        Material[] lightMaterial = {
                lightLoc.getBlock().getType(),
                lightLoc.add(1, 0, 0).getBlock().getType(),
                lightLoc.add(-2, 0, 0).getBlock().getType(),
                lightLoc.add(1, 0, 1).getBlock().getType(),
                lightLoc.add(0, 0, -2).getBlock().getType()
        };

        Block[] lightBlock = {
                lightLoc.getBlock(),
                lightLoc.add(1, 0, 0).getBlock(),
                lightLoc.add(-2, 0, 0).getBlock(),
                lightLoc.add(1, 0, 1).getBlock(),
                lightLoc.add(0, 0, -2).getBlock()
        };

        for (Block b : lightBlock) {
            b.setType(Material.LIGHT);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < lightBlock.length; i++) {
                    Block b = lightBlock[i];
                    b.setType(lightMaterial[i]);
                }
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 2 * 20);
    }

    private void burnGround(Location loc, Entity caster, UUID uuid) {
        int burnRadius = 8;
        for (int i = 2; i > -4; i--) {
            for (int x = -burnRadius; x <= burnRadius; x++) {
                for (int z = -burnRadius; z <= burnRadius; z++) {
                    if ((x * x) + (z * z) <= Math.pow(burnRadius, 2)) {
                        Block block = caster.getWorld().getBlockAt((int) loc.getX() + x, (int) loc.getY() + i, (int) loc.getZ() + z);
                        if (block.getType() == Material.DIRT || block.getType() == Material.DIRT_PATH || block.getType() == Material.COARSE_DIRT || block.getType() == Material.ROOTED_DIRT || block.getType() == Material.GRASS_BLOCK) {
                            logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                            block.setType(Material.NETHERRACK);
                        }
                        if (block.getType() == Material.STONE || block.getType() == Material.COBBLESTONE || block.getType() == Material.DIORITE || block.getType() == Material.ANDESITE || block.getType() == Material.GRANITE || block.getType() == Material.DEEPSLATE || block.getType() == Material.TUFF || block.getType() == Material.CALCITE || block.getType() == Material.GRAVEL) {
                            logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                            block.setType(Material.BASALT);
                        }
                        if (block.getType() == Material.WATER) {
                            logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                            block.setType(Material.AIR);
                        }
                        if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                            Random rand = new Random();
                            if (rand.nextInt(4) == 0) {
                                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                                block.setType(Material.FIRE);
                            }
                        }
                        if (block.getType() == Material.SAND || block.getType() == Material.RED_SAND) {
                            logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                            block.setType(Material.GLASS);
                        }
                    }
                }
            }
        }
    }

    private void damageNearbyEntities(Location loc, Entity caster, double multiplier) {
        ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc, 15, 15, 15);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity livingEntity) {
                if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                    ((Damageable) entity).damage(32 * multiplier, caster);
                    entity.setFireTicks(100);
                } else {
                    if (entity != caster) {
                        ((Damageable) entity).damage(18 * multiplier, caster);
                        entity.setFireTicks(100);
                    }
                }
            }
        }
    }

    private void spawnGroundParticles(Location loc) {
        loc.add(0, 1, 0);
        Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);

        new BukkitRunnable() {
            double radiusFlame = 1.8;

            @Override
            public void run() {
                radiusFlame = radiusFlame + 0.75;
                for (int j = 0; j < 150; j++) {
                    double x = radiusFlame * Math.cos(j);
                    double z = radiusFlame * Math.sin(j);
                    loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + x, loc.getY(), loc.getZ() + z, 5, 0, 0, 0, 0);
                }

                if (radiusFlame >= 11) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);

        new BukkitRunnable() {
            double radius = 1.8;

            @Override
            public void run() {
                radius = radius + 0.75;
                for (int i = 0; i < 150; i++) {
                    double x = radius * Math.cos(i);
                    double z = radius * Math.sin(i);
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0, 0, 0, 0);
                    loc.getWorld().spawnParticle(Particle.FIREWORK, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, 0.1, 0, 0.1, 0.15);
                    loc.getWorld().spawnParticle(Particle.DUST, loc.getX() + x + 0.2, loc.getY(), loc.getZ() + z + 0.2, 3, dustRipple);
                }

                if (radius >= 14) {
                    cancel();
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 4, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        p = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(p, 22);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }
        Location loc = lastBlock.getLocation();

        executeAbility(loc, p, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.RAW_GOLD, "Святість", "275", identifier);
    }
}
