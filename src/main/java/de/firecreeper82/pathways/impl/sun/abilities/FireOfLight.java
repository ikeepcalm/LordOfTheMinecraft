package de.firecreeper82.pathways.impl.sun.abilities;

import de.firecreeper82.lotm.Plugin;
import de.firecreeper82.pathways.Ability;
import de.firecreeper82.pathways.Pathway;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FireOfLight extends Ability {
    public FireOfLight(int identifier, Pathway pathway) {
        super(identifier, pathway);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        //get block player is looking at
        BlockIterator iter = new BlockIterator(p, 15);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }

        //setting the fire
        Location loc = lastBlock.getLocation().add(0, 1, 0);
        if(!loc.getBlock().getType().isSolid())
            loc.getBlock().setType(Material.FIRE);
        loc.add(1, 0, 0);
        if(!loc.getBlock().getType().isSolid())
            loc.getBlock().setType(Material.FIRE);
        loc.add(-2, 0, 0);
        if(!loc.getBlock().getType().isSolid())
            loc.getBlock().setType(Material.FIRE);
        loc.add(1, 0, -1);
        if(!loc.getBlock().getType().isSolid())
            loc.getBlock().setType(Material.FIRE);
        loc.add(0, 0, 2);
        if(!loc.getBlock().getType().isSolid())
            loc.getBlock().setType(Material.FIRE);
        loc.subtract(0, 0, 1);

        loc.add(0.5, 0.5, 0.5);

        final Material[] lightBlock = {loc.getBlock().getType()};
        loc.getBlock().setType(Material.LIGHT);

        AtomicInteger counter = new AtomicInteger();
        new BukkitRunnable() {
            @Override
            public void run() {
                counter.getAndIncrement();

                loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 0.75, 0.75, 0.75, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc, 8, 0.75, 0.75, 0.75, 0);

                //damage nearby entities
                ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
                for(Entity entity : nearbyEntities) {
                    if(entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (livingEntity.getCategory() == EntityCategory.UNDEAD) {
                            ((Damageable) entity).damage(10, p);
                            livingEntity.setFireTicks(10 * 20);
                        }
                        if(livingEntity.getUniqueId() != pathway.getUuid())
                            livingEntity.setFireTicks(10 * 20);

                    }
                }

                if(counter.get() >= 5 * 20) {
                    loc.getBlock().setType(Material.AIR);
                    cancel();
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    loc.getBlock().setType(lightBlock[0]);
                }
            }
        }.runTaskTimer(Plugin.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        ItemStack currentItem = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.setDisplayName("§6Fire of Light");
        itemMeta.addEnchant(Enchantment.CHANNELING, 4, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.values());
        ArrayList<String> lore = new ArrayList<>();
        lore.clear();
        lore.add("§5Click to use");
        lore.add("§5Spirituality: §720");
        lore.add("§8§l-----------------");
        lore.add("§6Sun - Pathway (7)");
        lore.add("§8" + Bukkit.getPlayer(pathway.getUuid()).getName());
        itemMeta.setLore(lore);
        currentItem.setItemMeta(itemMeta);
        return currentItem;
    }
}
