package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class IceAge extends Ability {

    public IceAge(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        UUID uuid = UUID.randomUUID();

        ArrayList<Block> blocks = GeneralPurposeUtil.getNearbyBlocksInSphere(p.getLocation(), 100, false, true, true);

        for (Block block : blocks) {
            block.setBiome(Biome.ICE_SPIKES);

            if (block.getType() == Material.WATER) {
                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                block.setType(Material.ICE);
                continue;
            }
            if (block.getType().getHardness() >= 5 || block.getType().getHardness() < 0)
                continue;

            if (block.getType().getHardness() < .2) {
                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                block.setType(Material.AIR);
            }
            if (block.getType().getHardness() < 1) {
                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                block.setType(Material.ICE);
            } else if (block.getType().getHardness() < 3) {
                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                block.setType(Material.PACKED_ICE);
            } else if (block.getType().getHardness() > 0) {
                logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                block.setType(Material.BLUE_ICE);
            }

            Block b = block.getLocation().add(0, 1, 0).getBlock();

            if (!b.getType().isSolid()) {
                logBlockBreak(uuid, new CustomLocation(b.getLocation()));
                b.setType(Material.SNOW);
            }
        }

        p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getEyeLocation(), 15000, 100, 100, 100, 0);

        for (Entity entity : p.getNearbyEntities(150, 150, 150)) {
            if (!(entity instanceof LivingEntity livingEntity))
                continue;

            livingEntity.damage(25, p);
            livingEntity.setFreezeTicks(20 * 60);
        }

        rollbackChanges(uuid);
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.BLUE_ICE, "Льодовиковий Період", "70000", identifier);
    }
}
