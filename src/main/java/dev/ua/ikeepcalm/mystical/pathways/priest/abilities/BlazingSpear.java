package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
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

import java.util.ArrayList;
import java.util.Objects;

public class BlazingSpear extends Ability {

    private boolean teleport = false;
    private final Particle.DustOptions dust;

    public BlazingSpear(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        dust = new Particle.DustOptions(Color.fromRGB(165, 0, 0), .5f);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        BlockIterator iter = new BlockIterator(player, 40);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }


        double distance = lastBlock.getLocation().distance(player.getEyeLocation());

        Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(distance)).clone();

        float angle = player.getEyeLocation().getYaw() / 60;

        Location spearLocation = player.getEyeLocation().subtract(Math.cos(angle), 0, Math.sin(angle));
        Vector dir = loc.toVector().subtract(spearLocation.toVector()).normalize();
        Vector direction = dir.clone();

        buildSpear(spearLocation.clone(), dir);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                spearLocation.add(direction);
                buildSpear(spearLocation.clone(), direction.clone());

                if (!Objects.requireNonNull(spearLocation.getWorld()).getNearbyEntities(spearLocation, 5, 5, 5).isEmpty()) {
                    for (Entity entity : spearLocation.getWorld().getNearbyEntities(spearLocation, 5, 5, 5)) {
                        if (entity instanceof LivingEntity) {
                            // Ignore player that initiated the shot
                            if (entity == player) {
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
                                ((Damageable) entity).damage(40 * getMultiplier(), player);
                                entity.setFireTicks(20 * 10);

                                pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                                cancel();
                                return;
                            }
                        }
                    }
                }

                //hits solid block
                if (spearLocation.getBlock().getType().isSolid()) {
                    Location fireLoc = spearLocation.clone();
                    ArrayList<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(fireLoc.getBlock(), 13, true);

                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;

                    player.getWorld().spawnParticle(Particle.DRIPPING_LAVA, fireLoc, 200, 5, 5, 5, 0);

                    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                        if (!(entity instanceof LivingEntity livingEntity))
                            continue;

                        livingEntity.damage(30 * getMultiplier(), player);
                        livingEntity.setFireTicks(20 * 6);
                    }
                    if (teleport) {
                        player.teleport(fireLoc);
                    }


                    cancel();
                }
                if (counter >= 160) {
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    cancel();
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
    public void leftClick() {
        if (pathway.getSequence().getCurrentSequence() < 7) {
            teleport = !teleport;
            if (player != null) {
                player.sendMessage("§cТелепорт " + (teleport ? "увімкнено" : "вимкнено"));
            }
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.BLAZE_ROD, "Палаючий спис", "80", identifier);
    }
}
