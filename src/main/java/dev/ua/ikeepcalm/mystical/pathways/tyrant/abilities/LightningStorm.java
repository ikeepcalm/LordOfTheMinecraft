package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantSequence;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
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
        player = pathway.getBeyonder().getPlayer();
        items.addToSequenceItems(identifier - 1, sequence);
        destruction = false;
    }

    @Override
    public void useAbility() {
        try {
            player = pathway.getBeyonder().getPlayer();
            Vector dir = player.getLocation().getDirection().normalize();
            Location loc = player.getEyeLocation();
            if (loc.getWorld() == null)
                return;

            for (int i = 0; i < 80; i++) {
                boolean entitiesNearby = loc.getWorld().getNearbyEntities(loc, 1, 1, 1).stream()
                        .anyMatch(entity -> entity.getType() == EntityType.ARMOR_STAND || entity == player);

                if (entitiesNearby)
                    break;

                loc.add(dir);

                if (loc.getBlock().getType().isSolid())
                    break;
            }

            BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();
            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                try {
                    loc.getWorld().setClearWeatherDuration(0);
                    loc.getWorld().setStorm(true);
                    loc.getWorld().setThunderDuration(120 * 60 * 20);
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Lightning Storm - Weather Control");
                }
            });

            executeAbility(loc, player, getMultiplier());
        } catch (Exception e) {
            ErrorLoggerUtil.logAbility(e, "Lightning Storm");
        }
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.LIGHT_BLUE_DYE, "Грозова Симфонія", "3000", identifier);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (loc.getWorld() == null)
            return;

        Random random = new Random();
        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();
        CompletableFuture.runAsync(() -> {
            try {
                int counter = 10 * 30;
                while (counter > 0) {
                    Location lightningLoc = loc.clone().add(random.nextInt(-25, 25), 0, random.nextInt(-25, 25));
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> spawnLighting(lightningLoc, caster, multiplier));

                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    counter--;
                }
            } catch (Exception e) {
                ErrorLoggerUtil.logAbility(e, "Lightning Storm - Asynchronous Task");
            }
        });
    }

    private void spawnLighting(Location loc, Entity caster, double multiplier) {
        try {
            Integer sequence = pathway.getSequence().getCurrentSequence();
            TyrantSequence.spawnLighting(loc, caster, multiplier, destruction, sequence);
        } catch (Exception e) {
            ErrorLoggerUtil.logAbility(e, "Lightning Storm - Spawn Lightning");
        }
    }

    @Override
    public void leftClick() {
        destruction = !destruction;
        Destruction = destruction ? "увімкнено" : "вимкнено";
        player.sendMessage("§aЗнищення блоків: §7" + Destruction);
    }
}
