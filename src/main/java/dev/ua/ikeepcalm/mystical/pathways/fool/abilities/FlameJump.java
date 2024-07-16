package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlameJump extends Ability {

    public FlameJump(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    Block teleportBlock;
    boolean justTeleported = false;

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        if (teleportBlock == null)
            return;

        Location loc = teleportBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        loc.setDirection(player.getLocation().getDirection());

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();
        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 1, false, false));
            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FLAME, loc.clone().add(0, -0.25, 0), 120, 0.3, 1, 0.3, 0.01);
            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.SMOKE, loc.clone().add(0, -0.25, 0), 85, 0.3, 1, 0.3, 0.015);
            player.teleport(loc);
        });

        justTeleported = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                scheduler.runTask(LordOfTheMinecraft.instance, () -> player.setFireTicks(0));
                justTeleported = false;
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 15);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.BLAZE_POWDER, "Вогняний Стрибок", "25", identifier);
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(Objects.requireNonNull(location.getWorld()).getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    @Override
    public void onHold() {
        player = pathway.getBeyonder().getPlayer();

        Vector direction = player.getLocation().getDirection().normalize();
        final Location[] loc = {player.getEyeLocation().clone()};

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();
        scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            for (int i = 0; i < 60; i++) {
                loc[0].add(direction);
                if (loc[0].getBlock().getType().isSolid())
                    break;
            }
            double nearestBlockDistance = -1;
            Block nearestBlock = null;

            List<Block> blocks = getNearbyBlocks(player.getLocation(), 60);

            for (Block b : blocks) {
                Material[] validMaterials = {
                        Material.FIRE,
                        Material.SOUL_FIRE,
                        Material.SOUL_CAMPFIRE,
                        Material.CAMPFIRE
                };
                if (!Arrays.asList(validMaterials).contains(b.getType()))
                    continue;
                if (nearestBlockDistance == -1) {
                    nearestBlock = b;
                    nearestBlockDistance = b.getLocation().distance(loc[0]);
                    continue;
                }
                if (nearestBlockDistance > b.getLocation().distance(loc[0])) {
                    nearestBlock = b;
                    nearestBlockDistance = b.getLocation().distance(loc[0]);
                }
            }

            if (nearestBlock == null) {
                teleportBlock = null;
                return;
            }

            loc[0] = nearestBlock.getLocation().clone();

            Block finalNearestBlock = nearestBlock;
            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                if (!justTeleported)
                    player.spawnParticle(Particle.FLASH, loc[0].clone().add(0.5, 0.75, 0.5), 1, 0, 0, 0, 0);
                teleportBlock = finalNearestBlock;
            });
        });
    }
}
