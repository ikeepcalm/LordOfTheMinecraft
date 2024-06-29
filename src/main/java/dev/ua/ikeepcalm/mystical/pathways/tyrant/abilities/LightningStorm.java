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

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class LightningStorm extends Ability {

    boolean destruction;
    String Destruction;

    public LightningStorm(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        p = pathway.getBeyonder().getPlayer();

        items.addToSequenceItems(identifier - 1, sequence);

        destruction = false;
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        Vector dir = p.getLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();
        if (loc.getWorld() == null)
            return;

        outerloop:
        for (int i = 0; i < 80; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (entity.getType() == EntityType.ARMOR_STAND || entity == p)
                    continue;
                break outerloop;
            }

            loc.add(dir);

            if (loc.getBlock().getType().isSolid()) {
                break;
            }
        }

        loc.getWorld().setClearWeatherDuration(0);
        loc.getWorld().setStorm(true);
        loc.getWorld().setThunderDuration(120 * 60 * 20);

        executeAbility(loc, p, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.LIGHT_BLUE_DYE, "Грозова Симфонія", "750", identifier);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (loc.getWorld() == null)
            return;

        Random random = new Random();
        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        CompletableFuture.runAsync(() -> {
            int counter = 10 * 30;
            while (counter > 0) {
                Location lightningLoc = loc.clone().add(random.nextInt(-25, 25), 0, random.nextInt(-25, 25));
                scheduler.runTask(LordOfTheMinecraft.instance, () -> spawnLighting(lightningLoc, caster, multiplier));

                try {
                    Thread.sleep(40); // Sleep for 2 ticks (40ms)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                counter--;
            }
        });
    }

    private void spawnLighting(Location loc, Entity caster, double multiplier) {
        Integer sequence = pathway.getSequence().getCurrentSequence();
        TyrantSequence.spawnLighting(loc, caster, multiplier, destruction, sequence);
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
