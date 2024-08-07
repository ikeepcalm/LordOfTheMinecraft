package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

import java.util.Objects;

public class Illuminate extends Ability {
    public Illuminate(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        player = pathway.getBeyonder().getPlayer();

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(player, 9);
        Block lastBlock = iter.next();
        Block previousBlock;
        while (iter.hasNext()) {
            previousBlock = lastBlock;
            lastBlock = iter.next();
            if (lastBlock.getType().isSolid()) {
                lastBlock = previousBlock;
                break;
            }
        }
        Location loc = lastBlock.getLocation();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
            loc.getBlock().setType(Material.LIGHT);
            loc.add(0.5, 0.5, 0.5);
        });

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {
                    counter++;

                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        double x = Math.cos(counter);
                        double z = Math.sin(counter);
                        double y = Math.sin(counter);
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FLAME, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0, 0, 0, 0);
                        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + x, loc.getY() + y, loc.getZ(), 1, 0, 0, 0, 0);
                        y = Math.cos(counter);
                        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY() + y, loc.getZ() + z, 1, 0, 0, 0, 0);

                        loc.getWorld().spawnParticle(Particle.END_ROD, loc, 5, 0.25, 0.25, 0.25, 0);
                    });

                    if (counter == 2 * 20) {
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    }

                    if (counter >= 15 * 20) {
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> loc.getBlock().setType(Material.AIR));
                        cancel();
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Illuminate");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.GOLD_NUGGET, "Ліхтарик", "20", identifier);
    }
}
