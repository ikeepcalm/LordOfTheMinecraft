package dev.ua.ikeepcalm.entities.mob.abilities;

import dev.ua.ikeepcalm.mystical.parents.abilities.MobAbility;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SpawnVex extends MobAbility {

    public SpawnVex(int frequency) {
        super(frequency);
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void useAbility(Location startLoc, Location endLoc, double multiplier, Entity user, Entity target) {
        Random random = new Random();
        try {
            for (int i = 0; i < random.nextInt(5) + 1; i++) {
                Location spawnLoc = startLoc.add(random.nextInt(6) - 3, random.nextInt(6) - 3, random.nextInt(6) - 3);
                user.getWorld().spawnEntity(spawnLoc, EntityType.VEX);
            }
        } catch (Exception e) {
            ErrorLoggerUtil.logAbility(e, "SpawnVex");
        }
    }
}
