package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class NatureSpirit extends Ability {

    public NatureSpirit(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= 20) {
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> pathway.getSequence().removeSpirituality(2000));
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        List<Block> blocks = GeneralPurposeUtil.getBlocksInSquare(player.getLocation().getBlock(), 10, true);
                        for (Block block : blocks) {
                            Material type = block.getType();
                            if (Tag.CROPS.isTagged(type)) {
                                block.applyBoneMeal(BlockFace.UP);
                                block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5, 0.1);
                            } else if (type == Material.GRASS_BLOCK) {
                                for (BlockFace face : BlockFace.values()) {
                                    Block adjacent = block.getRelative(face);
                                    if (adjacent.getType() == Material.DIRT) {
                                        adjacent.setType(Material.GRASS_BLOCK);
                                        block.getWorld().spawnParticle(Particle.COMPOSTER, adjacent.getLocation().add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5, 0.1);
                                    }
                                }
                            } else if (type == Material.BAMBOO || type == Material.CACTUS || type == Material.SUGAR_CANE) {
                                if (getStackHeight(block, type) < 3) {
                                    Block topBlock = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
                                    if (topBlock.getType() == Material.AIR) {
                                        topBlock.setType(type);
                                        block.getWorld().spawnParticle(Particle.COMPOSTER, topBlock.getLocation().add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5, 0.1);
                                    }
                                }
                            }
                        }
                    });
                    counter = 0;
                }
                counter++;

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1));
    }

    private int getStackHeight(Block baseBlock, Material type) {
        int height = 1;
        Block currentBlock = baseBlock;
        while (currentBlock.getRelative(BlockFace.UP).getType() == type) {
            height++;
            currentBlock = currentBlock.getRelative(BlockFace.UP);
        }
        currentBlock = baseBlock;
        while (currentBlock.getRelative(BlockFace.DOWN).getType() == type) {
            height++;
            currentBlock = currentBlock.getRelative(BlockFace.DOWN);
        }
        return height;
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.CLOCK, "Дух Природи", "2000/c", identifier);
    }
}
