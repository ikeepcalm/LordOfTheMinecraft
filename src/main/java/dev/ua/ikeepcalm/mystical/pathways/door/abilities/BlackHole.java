package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class BlackHole extends Ability {

    public BlackHole(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);

        for (int i = 0; i < 18; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir.multiply(2));

        if (loc.getWorld() == null)
            return;

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 0, 0), 2f);
        Random random = new Random();

        // Asynchronous task for long-running operations
        Bukkit.getScheduler().runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            final ArrayList<Block>[] blocks = new ArrayList[]{GeneralPurposeUtil.getNearbyBlocksInSphere(loc.getBlock().getLocation(), 32, false, true, true)};
            UUID uuid = UUID.randomUUID();

            // Synchronous task to interact with Spigot API
            new BukkitRunnable() {
                int counter = 0;
                int maxLife = 20 * 20;

                @Override
                public void run() {
                    try {
                        if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                            cancel();
                            return;
                        }

                        GeneralPurposeUtil.drawSphere(loc, 1, 20, dust, null, 0);

                        counter++;

                        if (counter >= 3 * 20) {
                            counter = 0;
                            blocks[0] = GeneralPurposeUtil.getNearbyBlocksInSphere(loc.getBlock().getLocation(), 32, false, true, true);
                        }

                        if (maxLife-- <= 0) {
                            cancel();
                            return;
                        }

                        for (int i = 0; i < 5; i++) {
                            if (blocks[0].isEmpty())
                                continue;
                            Block b = blocks[0].get(random.nextInt(blocks[0].size()));

                            Material blockMaterial = b.getType();
                            logBlockBreak(uuid, new CustomLocation(b.getLocation()));
                            b.setType(Material.AIR);

                            if (blockMaterial == Material.WATER || blockMaterial == Material.LAVA)
                                continue;

                            FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation().clone().add(0, 1, 0), blockMaterial.createBlockData());
                            fallingBlock.setGravity(false);
                            fallingBlock.setDropItem(false);

                            if (fallingBlock.getBlockData().getMaterial() == Material.WATER) {
                                fallingBlock.remove();
                                continue;
                            }

                            Vector dir = loc.clone().toVector().subtract(fallingBlock.getLocation().toVector()).normalize().multiply(.55);
                            fallingBlock.setVelocity(dir);

                            new BukkitRunnable() {
                                int life = 20 * 6;

                                @Override
                                public void run() {
                                    if (!fallingBlock.isValid()) {
                                        cancel();
                                        return;
                                    }

                                    life--;

                                    if (life <= 0 || fallingBlock.getBlockData().getMaterial() == Material.WATER) {
                                        fallingBlock.remove();
                                        cancel();
                                        return;
                                    }

                                    Vector direction = loc.clone().toVector().subtract(fallingBlock.getLocation().toVector()).normalize().multiply(.55);
                                    fallingBlock.setVelocity(direction);

                                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                                        fallingBlock.remove();
                                    }
                                }
                            }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1); // Changed the interval to 1 tick for smoother movement
                        }
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Black Hole");
                        cancel();
                    }
                }

                @Override
                public void cancel() {
                    super.cancel();
                    rollbackChanges(uuid);
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1); // Changed the interval to 1 tick for smoother movement

            // Asynchronous task for entity handling
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {


                        if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                            cancel();
                            return;
                        }

                        if (loc.getWorld() == null)
                            return;

                        // Run entity interactions on the main thread
                        Bukkit.getScheduler().runTask(LordOfTheMinecraft.instance, () -> {
                            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 30, 30, 30)) {
                                if (entity == caster)
                                    continue;

                                Vector dir = loc.clone().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(.5);
                                entity.setVelocity(dir);
                            }

                            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
                                if (!(entity instanceof LivingEntity livingEntity)) {
                                    if (!(entity instanceof Item))
                                        entity.remove();
                                    continue;
                                }

                                livingEntity.damage(8 * multiplier, caster);
                            }
                        });
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Black Hole");
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(LordOfTheMinecraft.instance, 0, 1); // Changed the interval to 1 tick for smoother handling
        });
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        executeAbility(player.getEyeLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.ENDERMAN_SPAWN_EGG, "Чорна Діра", "7000", identifier);
    }
}
