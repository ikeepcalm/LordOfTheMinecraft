package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public class FrostSpear extends Ability {

    private final Material[] convertMaterials;
    private final Particle.DustOptions dust;

    public FrostSpear(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        dust = new Particle.DustOptions(Color.fromRGB(165, 231, 250), .5f);
        convertMaterials = new Material[]{
                Material.GRASS_BLOCK,
                Material.DIRT_PATH,
                Material.DIRT,
                Material.ROOTED_DIRT,
                Material.MYCELIUM,
                Material.PODZOL,
                Material.STONE,
                Material.GRANITE,
                Material.DIORITE,
                Material.ANDESITE,
                Material.GRAVEL,
                Material.SAND
        };
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        double multiplier = getMultiplier();

        //get block player is looking at
        BlockIterator iter = new BlockIterator(p, 40);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }

        double distance = lastBlock.getLocation().distance(p.getEyeLocation());

        Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(distance)).clone();

        float angle = p.getEyeLocation().getYaw() / 60;

        Location spearLocation = p.getEyeLocation().subtract(Math.cos(angle), 0, Math.sin(angle));
        Vector dir = loc.toVector().subtract(spearLocation.toVector()).normalize();
        Vector direction = dir.clone();

        buildSpear(spearLocation.clone(), dir);

        new BukkitRunnable() {
            int counter = 0;
            final UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                spearLocation.add(direction);
                buildSpear(spearLocation.clone(), direction.clone());

                if (!Objects.requireNonNull(spearLocation.getWorld()).getNearbyEntities(spearLocation, 5, 5, 5).isEmpty()) {
                    for (Entity entity : spearLocation.getWorld().getNearbyEntities(spearLocation, 5, 5, 5)) {
                        if (entity instanceof LivingEntity) {
                            // Ignore player that initiated the shot
                            if (entity == p) {
                                continue;
                            }
                            Vector particleMinVector = new Vector(
                                    spearLocation.getX() - 0.25,
                                    spearLocation.getY() - 0.25,
                                    spearLocation.getZ() - 0.25);
                            Vector particleMaxVector = new Vector(
                                    spearLocation.getX() + 0.25,
                                    spearLocation.getY() + 0.25,
                                    spearLocation.getZ() + 0.25);

                            //entity hit
                            if (entity.getBoundingBox().overlaps(particleMinVector, particleMaxVector)) {

                                entity.setVelocity(entity.getVelocity().add(spearLocation.getDirection().normalize().multiply(1.5)));
                                ((Damageable) entity).damage(28 * multiplier, p);
                                entity.setFreezeTicks(20 * 10);


                                pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                                cancel();
                                return;
                            }
                        }
                    }
                }

                //hits solid block
                if (spearLocation.getBlock().getType().isSolid()) {
                    Location freezeLoc = spearLocation.clone();
                    ArrayList<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(freezeLoc.getBlock(), 13, true);

                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;

                    Random random = new Random();

                    for (Block block : blocks) {
                        if (!Arrays.asList(convertMaterials).contains(block.getType()))
                            continue;

                        if (random.nextInt(4) == 0)
                            continue;

                        logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                        block.setType(Material.PACKED_ICE);
                    }

                    p.getWorld().spawnParticle(Particle.SNOWFLAKE, freezeLoc, 200, 5, 5, 5, 0);

                    for (Entity entity : p.getNearbyEntities(10, 10, 10)) {
                        if (!(entity instanceof LivingEntity livingEntity))
                            continue;

                        livingEntity.damage(8, p);
                        livingEntity.setFreezeTicks(20 * 6);
                    }
                    cancel();
                }
                if (counter >= 160) {
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    cancel();
                    rollbackChanges(uuid);
                    return;
                }
                counter++;
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 5, 0);

        new BukkitRunnable() {
            public void run() {
                pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 20);
    }

    public void buildSpear(Location loc, Vector direc) {

        for (int i = 0; i < 6; i++) {
            loc.subtract(direc);
        }

        int circlePoints = 10;
        double radius = 0.2;
        Location playerLoc = loc.clone();
        Vector dir = loc.clone().getDirection().normalize().multiply(0.15);
        double pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
        double yaw = -playerLoc.getYaw() * 0.017453292F;
        double increment = (2 * Math.PI) / circlePoints;
        for (int k = 0; k < 5; k++) {
            radius -= 0.009;
            for (int i = 0; i < circlePoints; i++) {
                double angle = i * increment;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                Vector vec = new Vector(x, 0, z);
                MathVectorUtils.rotateAroundAxisX(vec, pitch);
                MathVectorUtils.rotateAroundAxisY(vec, yaw);
                playerLoc.subtract(vec);
                Objects.requireNonNull(playerLoc.getWorld()).spawnParticle(Particle.DUST, playerLoc.clone(), 1, 0, 0, 0, dust);
                playerLoc.add(vec);
            }
            playerLoc.subtract(dir);
        }

        direc.multiply(0.125);
        for (int i = 0; i < 96; i++) {
            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc.clone(), 10, .03, .03, .03, dust);
            loc.add(direc);
        }

        circlePoints = 20;
        radius = 0.3;
        playerLoc = loc.clone();
        dir = loc.clone().getDirection().normalize().multiply(0.15);
        pitch = (playerLoc.getPitch() + 90.0F) * 0.017453292F;
        yaw = -playerLoc.getYaw() * 0.017453292F;
        increment = (2 * Math.PI) / circlePoints;
        for (int k = 0; k < 13; k++) {
            radius -= 0.019;
            for (int i = 0; i < circlePoints; i++) {
                double angle = i * increment;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                Vector vec = new Vector(x, 0, z);
                MathVectorUtils.rotateAroundAxisX(vec, pitch);
                MathVectorUtils.rotateAroundAxisY(vec, yaw);
                playerLoc.add(vec);
                Objects.requireNonNull(playerLoc.getWorld()).spawnParticle(Particle.DUST, playerLoc.clone(), 1, 0, 0, 0, dust);
                playerLoc.subtract(vec);
            }
            playerLoc.add(dir);
        }
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.IRON_SWORD, "Крижаний Спис", "300", identifier);
    }
}
