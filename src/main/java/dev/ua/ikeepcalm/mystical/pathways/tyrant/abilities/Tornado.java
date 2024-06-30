package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Tornado extends Ability {

    public Tornado(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        if (p == null) return;

        Vector dir = p.getLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();
        if (loc.getWorld() == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 80; i++) {
                        Location currentLoc = loc.clone().add(dir.multiply(i));
                        if (currentLoc.getBlock().getType().isSolid()) break;

                        boolean entityFound = false;
                        for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1)) {
                            if (entity instanceof LivingEntity && entity != p) {
                                entityFound = true;
                                break;
                            }
                        }
                        if (entityFound) break;
                    }

                    executeAbility(loc, p, getMultiplier());
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Tornado");
                    cancel();
                }
            }
        }.runTask(LordOfTheMinecraft.instance);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (!(caster instanceof LivingEntity livingEntity)) return;

        LordOfTheMinecraft.instance.getServer().getScheduler().runTask(LordOfTheMinecraft.instance, () -> new dev.ua.ikeepcalm.entities.disasters.Tornado(livingEntity).spawnDisaster(livingEntity, loc));
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.WHITE_DYE, "Зов Смерчу", "500", identifier);
    }
}
