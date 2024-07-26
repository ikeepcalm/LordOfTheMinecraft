package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantSequence;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Lightning extends Ability {

    boolean destruction;
    String Destruction;

    public Lightning(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        player = pathway.getBeyonder().getPlayer();
        items.addToSequenceItems(identifier - 1, sequence);
        destruction = true;
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        Vector dir = player.getLocation().getDirection().normalize();
        Location loc = player.getEyeLocation();
        if (loc.getWorld() == null)
            return;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (final int[] i = {0}; i[0] < 80; i[0]++) {
                        final Location currentLoc = loc.clone();
                        try {
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                try {
                                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1)) {
                                        if (entity.getType() == EntityType.ARMOR_STAND || entity == player)
                                            continue;
                                        i[0] = 80;
                                        break;
                                    }
                                } catch (Exception e) {
                                    LoggerUtil.logAbilityError(e, "Lightning - Entity Check");
                                }
                            });
                        } catch (Exception e) {
                            LoggerUtil.logAbilityError(e, "Lightning - Scheduler Task");
                        }

                        if (i[0] >= 80 || loc.getBlock().getType().isSolid()) {
                            break;
                        }

                        loc.add(dir);
                    }

                    try {
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            try {
                                player.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL);
                                scheduler.runTaskLater(LordOfTheMinecraft.instance,
                                        () -> {
                                            try {
                                                player.resetPlayerWeather();
                                            } catch (Exception e) {
                                                LoggerUtil.logAbilityError(e, "Lightning - Weather Clear");
                                            }
                                        }, 20 * 20);
                            } catch (Exception e) {
                                LoggerUtil.logAbilityError(e, "Lightning - Weather Control");
                            }
                        });
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Lightning - Scheduler Task");
                    }

                    executeAbility(loc, player, getMultiplier());
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Lightning");
                    cancel();
                }
            }
        }.runTaskAsynchronously(LordOfTheMinecraft.instance);
    }


    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.LIGHT_BLUE_DYE, "Гуркіт Грому", "200", identifier);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (loc.getWorld() == null)
            return;

        Integer sequence = pathway.getSequence().getCurrentSequence();
        LordOfTheMinecraft.instance.getServer().getScheduler().runTask(
                LordOfTheMinecraft.instance,
                () -> {
                    try {
                        TyrantSequence.spawnLighting(loc, caster, multiplier, destruction, sequence);
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Lightning - Spawn Lighting");
                    }
                }
        );
    }

    @Override
    public void leftClick() {
        destruction = !destruction;
        if (destruction)
            Destruction = "увімкнено";
        else Destruction = "вимкнено";
        player.sendMessage("§aЗнищення блоків: §7" + Destruction);
    }
}
