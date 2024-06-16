package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ThreadManipulation extends Ability {

    public ThreadManipulation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        placeThreads(true, caster, loc);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        placeThreads(false, null, null);
    }

    private void placeThreads(boolean npc, Entity e, Location target) {
        Entity caster = npc ? e : p;

        Location loc = npc ? target : p.getEyeLocation();

        if (!npc) {
            Vector dir = p.getEyeLocation().getDirection().normalize();
            World world = loc.getWorld();

            if (world == null)
                return;

            for (int i = 0; i < 30; i++) {
                loc.add(dir);

                if (loc.getBlock().getType().isSolid())
                    break;

                if (!world.getNearbyEntities(loc, 1.2, 1.2, 1.2).isEmpty() && !world.getNearbyEntities(loc, 1.2, 1.2, 1.2).contains(p))
                    break;
            }
        }

        Random random = new Random();

        ArrayList<Block> blocks = GeneralPurposeUtil.getNearbyBlocksInSphere(loc, 7, false, false, true);

        HashMap<Block, Material> materials = new HashMap<>();

        for (Block block : blocks) {
            if (block.getType().isSolid())
                continue;

            if (random.nextInt(3) != 0)
                continue;

            materials.put(block, block.getType());
            block.setType(Material.COBWEB);

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(materials.get(block));
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 20 * 20);
        }
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.COBWEB, "Маніпуляція Павутинням", "40", identifier);
    }
}
