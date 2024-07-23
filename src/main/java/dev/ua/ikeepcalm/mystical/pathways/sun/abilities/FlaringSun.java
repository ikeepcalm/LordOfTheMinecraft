package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class FlaringSun extends Ability {

    public FlaringSun(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    private ArrayList<Block> airBlocks;

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        airBlocks = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        int burnRadius = 10;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    List<Block> blocksToChange = new ArrayList<>();

                    for (int i = 3; i > -8; i--) {
                        for (int x = -burnRadius; x <= burnRadius; x++) {
                            for (int z = -burnRadius; z <= burnRadius; z++) {
                                if ((x * x) + (z * z) <= Math.pow(burnRadius, 2)) {
                                    Block block = caster.getWorld().getBlockAt((int) loc.getX() + x, (int) loc.getY() + i, (int) loc.getZ() + z);
                                    if (block.getType() == Material.DIRT || block.getType() == Material.DIRT_PATH || block.getType() == Material.COARSE_DIRT || block.getType() == Material.ROOTED_DIRT || block.getType() == Material.GRASS_BLOCK) {
                                        blocksToChange.add(block);
                                    } else if (block.getType() == Material.STONE || block.getType() == Material.COBBLESTONE || block.getType() == Material.DIORITE || block.getType() == Material.ANDESITE || block.getType() == Material.GRANITE || block.getType() == Material.DEEPSLATE || block.getType() == Material.TUFF || block.getType() == Material.CALCITE || block.getType() == Material.GRAVEL) {
                                        blocksToChange.add(block);
                                    } else if (block.getType() == Material.WATER) {
                                        blocksToChange.add(block);
                                    } else if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                                        Random rand = new Random();
                                        if (rand.nextInt(4) == 0) {
                                            blocksToChange.add(block);
                                        }
                                    } else if (block.getType() == Material.SAND || block.getType() == Material.RED_SAND) {
                                        blocksToChange.add(block);
                                    }
                                }
                            }
                        }
                    }

                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        for (Block block : blocksToChange) {
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
                        rollbackChanges(uuid);
                    });
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Flaring Sun");
                    cancel();
                }
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);

        Location sphereLoc = loc.clone();
        new BukkitRunnable() {
            int counter = 0;
            public final double sphereRadius = 5;

            @Override
            public void run() {
                try {
                    counter++;

                    // Spawn particles
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FLAME, loc, 50, 2, 2, 2, 0);
                    loc.getWorld().spawnParticle(Particle.END_ROD, loc, 70, 2, 2, 2, 0);
                    for (double i = 0; i <= Math.PI; i += Math.PI / 15) {
                        double radius = Math.sin(i) * sphereRadius;
                        double y = Math.cos(i) * sphereRadius;
                        for (double a = 0; a < Math.PI * 2; a += Math.PI / 15) {
                            double x = Math.cos(a) * radius;
                            double z = Math.sin(a) * radius;
                            sphereLoc.add(x, y, z);
                            Particle.DustOptions dustSphere = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1f);
                            Objects.requireNonNull(sphereLoc.getWorld()).spawnParticle(Particle.DUST, sphereLoc, 1, 0.25, 0.25, 0.25, 0, dustSphere);
                            sphereLoc.getWorld().spawnParticle(Particle.FLAME, sphereLoc, 1, 0.25, 0.25, 0.25, 0);
                            if (counter == 1 && !sphereLoc.getBlock().getType().isSolid()) {
                                airBlocks.add(sphereLoc.getBlock());
                                sphereLoc.getBlock().setType(Material.LIGHT);
                            }
                            sphereLoc.subtract(x, y, z);
                        }
                    }

                    // Damage nearby entities
                    ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc, 10, 10, 10);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof LivingEntity livingEntity) {
                            if (Tag.ENTITY_TYPES_SENSITIVE_TO_SMITE.isTagged(entity.getType())) {
                                ((Damageable) entity).damage(7 * multiplier, caster);
                                livingEntity.setFireTicks(50 * 20);
                            } else if (entity != caster) {
                                livingEntity.setFireTicks(50 * 20);
                                ((Damageable) entity).damage(3 * multiplier, caster);
                            }
                        }
                    }

                    if (counter >= 20 * 20) {
                        for (Block b : airBlocks) {
                            b.setType(Material.AIR);
                        }
                        cancel();
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Flaring Sun");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        player = pathway.getBeyonder().getPlayer();

        double multiplier = getMultiplier();

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(player, 15);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }

        Location loc = lastBlock.getLocation().add(0, 1, 0);

        executeAbility(loc, player, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.SUNFLOWER, "Спалахуюче Сонце", "300", identifier);
    }
}
