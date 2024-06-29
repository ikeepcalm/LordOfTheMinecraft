package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantSequence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Lightning extends Ability {

    boolean destruction;
    String Destruction;

    public Lightning(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        p = pathway.getBeyonder().getPlayer();
        items.addToSequenceItems(identifier - 1, sequence);
        destruction = true;
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        Vector dir = p.getLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();
        if (loc.getWorld() == null)
            return;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        // Run the main logic asynchronously
        scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            for (final int[] i = {0}; i[0] < 80; i[0]++) {
                final Location currentLoc = loc.clone();
                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 1, 1, 1)) {
                        if (entity.getType() == EntityType.ARMOR_STAND || entity == p)
                            continue;
                        i[0] = 80;
                        break;
                    }
                });

                if (i[0] >= 80 || loc.getBlock().getType().isSolid()) {
                    break;
                }

                loc.add(dir);
            }

            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                loc.getWorld().setClearWeatherDuration(0);
                loc.getWorld().setStorm(true);
                loc.getWorld().setThunderDuration(120 * 60 * 20);
            });

            executeAbility(loc, p, getMultiplier());
        });
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
                () -> TyrantSequence.spawnLighting(loc, caster, multiplier, destruction, sequence)
        );
    }

    @Override
    public void leftClick() {
        destruction = !destruction;
        if (destruction)
            Destruction = "увімкнено";
        else Destruction = "вимкнено";
        p.sendMessage("§aЗнищення блоків: §7" + Destruction);
    }
}
