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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class LightningTornado extends Ability {

    public LightningTornado(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        player = pathway.getBeyonder().getPlayer();
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        Vector dir = player.getLocation().getDirection().normalize();
        Location loc = player.getEyeLocation();
        if (loc.getWorld() == null) return;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (final int[] i = {0}; i[0] < 80; i[0]++) {
                        final Location currentLoc = loc.clone();
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            try {
                                for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1)) {
                                    if (entity.getType() == EntityType.ARMOR_STAND || entity == player) continue;
                                    // Break the loop by setting i to 80
                                    i[0] = 80;
                                    break;
                                }
                            } catch (Exception e) {
                                ErrorLoggerUtil.logAbility(e, "Lightning Tornado - Entity Check");
                                cancel();
                            }
                        });

                        if (i[0] >= 80 || loc.getBlock().getType().isSolid()) {
                            break;
                        }

                        loc.add(dir);
                    }

                    executeAbility(loc, player, getMultiplier());
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Lightning Tornado");
                    cancel();
                }
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (!(caster instanceof LivingEntity livingEntity)) return;

        try {
            LordOfTheMinecraft.instance.getServer().getScheduler().runTask(LordOfTheMinecraft.instance, () -> new dev.ua.ikeepcalm.entities.disasters.LightningTornado(livingEntity).spawnDisaster(livingEntity, loc));
        } catch (Exception e) {
            ErrorLoggerUtil.logAbility(e, "Lightning Tornado - Spawn Disaster");
        }
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.BLUE_CANDLE, "Вихор Блискавок", "10000", identifier);
    }
}

