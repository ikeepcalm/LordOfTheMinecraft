package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class FlameControlling extends Ability {

    public FlameControlling(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
            if (pathway.getSequence().getCurrentSequence() == 7) {
                if (!player.getInventory().contains(Material.COAL) && !player.getInventory().contains(Material.CHARCOAL)) {
                    Location noFuelLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
                    if (noFuelLoc.getWorld() == null)
                        return;
                    noFuelLoc.getWorld().spawnParticle(Particle.SMOKE, noFuelLoc, 25, 0.05, 0.05, 0.05, 0.15);
                    return;
                }

                ItemStack item;
                for (int i = 0; i < player.getInventory().getContents().length; i++) {
                    item = player.getInventory().getItem(i);
                    if (item == null)
                        continue;
                    if (item.getType() == Material.COAL || item.getType() == Material.CHARCOAL) {
                        item.setAmount(item.getAmount() - 1);
                        player.getInventory().setItem(i, item);
                        break;
                    }
                }
            }
        });

        Vector direction = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0).clone();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                loc.add(direction);
                if (loc.getWorld() == null) {
                    cancel();
                    return;
                }

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.12, 0.12, 0.12, 0.025);
                    loc.getWorld().spawnParticle(Particle.SMOKE, loc.clone().add(0, 0.12, 0), 6, 0.01, 0.01, 0.01, 0);
                });

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    if (!loc.getWorld().getNearbyEntities(loc, 5, 5, 5).isEmpty()) {
                        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
                            Vector v1 = new Vector(
                                    loc.getX() + 0.25,
                                    loc.getY() + 0.25,
                                    loc.getZ() + 0.25
                            );
                            Vector v2 = new Vector(
                                    loc.getX() - 0.25,
                                    loc.getY() - 0.25,
                                    loc.getZ() - 0.25
                            );
                            if (entity.getBoundingBox().overlaps(v1, v2) && entity instanceof Damageable && entity != caster && entity.getType() != EntityType.ARMOR_STAND) {
                                ((Damageable) entity).damage(8 * multiplier, caster);
                                entity.setFireTicks(250);
                                cancel();
                                return;
                            }
                        }
                    }

                    if (loc.getBlock().getType().isSolid() || counter >= 100) {
                        if (loc.getBlock().getType().isSolid() && !loc.clone().add(0, 1, 0).getBlock().getType().isSolid())
                            loc.clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
                        cancel();
                    }
                });
            }
        }.runTaskTimerAsynchronously(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        double multiplier = getMultiplier();
        player = pathway.getBeyonder().getPlayer();
        executeAbility(player.getEyeLocation(), player, multiplier);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.FIRE_CHARGE, "Підкорення Вогню", "45", identifier);
    }
}
