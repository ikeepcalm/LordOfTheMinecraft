package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
        player.setVisualFire(false);
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
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromBGR(0, 40, 240), 1.0f);
                                block.getWorld().spawnParticle(Particle.DUST, block.getLocation().add(0.5, 1.5, 0.5), 15, 0.5, 0.5, 0.5, 0.5, dustOptions);
                            }

                        }
                    }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20);
                }
            }
        }
    }

    @EventHandler
    public void onTrapActivation(EntityMoveEvent event) {
        Block block = event.getTo().getBlock().getLocation().add(0, -1, 0).getBlock();
        if (block.hasMetadata("trap")) {
            double multiplier = getMultiplier();
            if (multiplier != 0) {
                event.getEntity().damage(2.0 * multiplier);
            } else {
                event.getEntity().damage(2.0);
            }
            event.getEntity().setFireTicks(60);
            event.getTo().getWorld().createExplosion(block.getLocation().add(0, 1, 0), 2.0f, true, false);
            block.removeMetadata("trap", LordOfTheMinecraft.instance);
        }
    }

    @EventHandler
    public void onTrapActivation(PlayerMoveEvent event) {
        Block block = event.getTo().getBlock().getLocation().add(0, -1, 0).getBlock();
        double multiplier = getMultiplier();
        Player player = event.getPlayer();
        if (block.hasMetadata("trap")) {
            if (LordOfTheMinecraft.beyonders.containsKey(player.getUniqueId())) {
                Beyonder beyonder = LordOfTheMinecraft.beyonders.get(player.getUniqueId());
                if (beyonder.getPathway().getNameNormalized().equalsIgnoreCase("priest")) {
                    return;
                }
            }
            player.sendMessage("§cВи потрапили в пастку!");
            if (multiplier != 0) {
                player.damage(5.0 * multiplier);
            } else {
                player.damage(5.0);
            }
            player.setFireTicks(60);
            event.getTo().getWorld().createExplosion(block.getLocation().add(0, 1, 0), 2.0f, true, false);
            block.removeMetadata("trap", LordOfTheMinecraft.instance);
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.FERMENTED_SPIDER_EYE, "Пастка", "30", identifier);
    }
}
