package de.firecreeper82.pathways.impl.sun.abilities;

import de.firecreeper82.lotm.Plugin;
import de.firecreeper82.pathways.Ability;
import de.firecreeper82.pathways.Pathway;
import de.firecreeper82.pathways.impl.sun.SunItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;

public class HolyLightSummoning extends Ability {
    public HolyLightSummoning(int identifier, Pathway pathway) {
        super(identifier, pathway);
    }
    @Override
    public void useAbility() {
        double multiplier = getMultiplier();

        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        //get block player is looking at
        BlockIterator iter = new BlockIterator(p, 22);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (!lastBlock.getType().isSolid()) {
                continue;
            }
            break;
        }
        Location loc = lastBlock.getLocation();
        loc.add(0, 14, 0);

        //Runnable
        final Material[] lastMaterial = {loc.getBlock().getType()};
        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                counter++;

                //Particles
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + 3.2, loc.getY(), loc.getZ(), 6, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 3.2, loc.getY(), loc.getZ(), 6, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY(), loc.getZ() + 3.2, 6, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY(), loc.getZ() - 3.2, 6, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + 2.4, loc.getY(), loc.getZ() + 2.4, 5, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 2.4, loc.getY(), loc.getZ() - 2.4, 5, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() - 2.4, loc.getY(), loc.getZ() + 2.4, 5, 0.1, 0, 0.1, 0);
                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + 2.4, loc.getY(), loc.getZ() - 2.4, 5, 0.1, 0, 0.1, 0);

                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 2.7, loc.getY(), loc.getZ(), 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 2.7, loc.getY(), loc.getZ(), 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ() + 2.7, 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ() - 2.7, 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 1.9, loc.getY(), loc.getZ() + 1.9, 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 1.9, loc.getY(), loc.getZ() - 1.9, 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() - 1.9, loc.getY(), loc.getZ() + 1.9, 12, 0.2, 0, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX() + 1.9, loc.getY(), loc.getZ() - 1.9, 12, 0.2, 0, 0.2, 0);


                Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.25f);
                for(double i = 0; i < 3.2; i+=0.8) {
                    for(int j = 0; j < 100; j++) {
                        double x = i * Math.cos(j);
                        double z = i * Math.sin(j);
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, dust);
                        if(j % 2 == 0)
                            loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.getX() + x, loc.getY() + 1, loc.getZ() + z, 1, 0, 0, 0, 0);
                    }
                }

                //light at current loc
                loc.getBlock().setType(lastMaterial[0]);
                loc.subtract(0, 1, 0);
                lastMaterial[0] = loc.getBlock().getType();
                loc.getBlock().setType(Material.LIGHT);

                if((lastMaterial[0].isSolid() && counter >= 12) || counter >= 200) {
                    loc.getBlock().setType(lastMaterial[0]);
                    counter = 0;
                    cancel();

                    //Light that stays at the ground for a bit
                    Location lightLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
                    Material[] lightMaterial = {
                            lightLoc.getBlock().getType(),
                            lightLoc.add(1, 0, 0).getBlock().getType(),
                            lightLoc.add(-2, 0, 0).getBlock().getType(),
                            lightLoc.add(1, 0, 1).getBlock().getType(),
                            lightLoc.add(0, 0, -2).getBlock().getType()
                    };

                    Block[] lightBlock = {
                            lightLoc.getBlock(),
                            lightLoc.add(1, 0, 0).getBlock(),
                            lightLoc.add(-2, 0, 0).getBlock(),
                            lightLoc.add(1, 0, 1).getBlock(),
                            lightLoc.add(0, 0, -2).getBlock()
                    };

                    for(Block b : lightBlock) {
                        b.setType(Material.LIGHT);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < lightBlock.length; i++) {
                                Block b = lightBlock[i];
                                b.setType(lightMaterial[i]);
                            }
                        }
                    }.runTaskLater(Plugin.instance, 2 * 20);


                    //damage nearby entities
                    ArrayList<Entity> nearbyEntities = (ArrayList<Entity>) loc.getWorld().getNearbyEntities(loc, 15, 15, 15);
                    for(Entity entity : nearbyEntities) {
                        if(entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            if (livingEntity.getCategory() == EntityCategory.UNDEAD) {
                                ((Damageable) entity).damage(22 * multiplier, p);
                            } else {
                                if(entity != p)
                                    ((Damageable) entity).damage(12 * multiplier, p);
                            }
                        }
                    }

                    //Particles on ground
                    loc.add(0, 1, 0);
                    Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(0, 215, 255), 1.5f);
                    new BukkitRunnable() {
                        double radius = 1.8;
                        int factor = 0;
                        @Override
                        public void run() {
                            radius = radius + 0.75;
                            for(int i = 0; i < 100; i++) {
                                factor++;
                                double x = radius * Math.cos(factor);
                                double z = radius * Math.sin(factor);
                                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0, 0, 0, 0);
                                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.getX() + x, loc.getY(), loc.getZ() + z, 2, 0.1, 0, 0.1, 0.15);
                                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX() + x + 0.2, loc.getY(), loc.getZ() + z + 0.2, 3, dustRipple);
                            }

                            if(radius >= 9) {
                                cancel();
                                pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                            }
                        }
                    }.runTaskTimer(Plugin.instance, 0, 1);
                }
            }
        }.runTaskTimer(Plugin.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        ItemStack currentItem = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = currentItem.getItemMeta();
        itemMeta.setDisplayName("§6Holy Light Summoning");
        itemMeta.addEnchant(Enchantment.CHANNELING, 5, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.values());
        ArrayList<String> lore = new ArrayList<>();
        lore.clear();
        lore.add("§5Click to use");
        lore.add("§5Spirituality: §730");
        lore.add("§8§l-----------------");
        lore.add("§6Sun - Pathway (7)");
        lore.add("§8" + Bukkit.getPlayer(pathway.getUuid()).getName());
        itemMeta.setLore(lore);
        currentItem.setItemMeta(itemMeta);
        return SunItems.createItem(Material.BLAZE_ROD, "Holy Light Summoning", "30", 5, 7, Bukkit.getPlayer(pathway.getUuid()).getName());
    }
}
