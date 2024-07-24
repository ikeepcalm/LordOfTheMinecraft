package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Trap extends Ability implements Listener {

    public Trap(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        Block block = player.getTargetBlock(null, 5);
        if (block.getType() == Material.AIR) {
            return;
        }

        block.setMetadata("trap", new FixedMetadataValue(LordOfTheMinecraft.instance, true));
        player.getInventory().removeItem(getItem());
        player.sendMessage("§cПастка була встановлена!");
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        if (event.getPlayer().getInventory().getItem(event.getNewSlot()).isSimilar(getItem())) {
            List<Block> blocks = GeneralPurposeUtil.getBlocksInSquare(event.getPlayer().getLocation().getBlock(), 5, true);
            for (Block block : blocks) {
                if (block.hasMetadata("trap")) {
                    new BukkitRunnable() {
                        int time = 0;

                        @Override
                        public void run() {
                            if (time == 5) {
                                cancel();
                            } else {
                                time++;
                                block.getWorld().spawnParticle(Particle.FLAME, block.getLocation().add(0, 1, 0), 5, 0.1, 0.1, 0.1, 0.1);
                            }

                        }
                    }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Block block = event.getTo().getBlock().getLocation().add(0, -1, 0).getBlock();
        if (block.hasMetadata("trap")) {
            event.getPlayer().sendMessage("§cВи потрапили в пастку!");
            event.getTo().getWorld().createExplosion(block.getLocation().add(0, 1, 0), 3.0f, true, false);
            event.getPlayer().setFireTicks(60);
            event.getPlayer().damage(5.0 * pathway.getSequence().getSequenceMultiplier().get(pathway.getSequence().getCurrentSequence()));
            block.removeMetadata("trap", LordOfTheMinecraft.instance);
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.SILVERFISH_SPAWN_EGG, "Пастка", "30", identifier);
    }
}
