package dev.ua.ikeepcalm.entities.disasters;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Blizzard extends Disaster {
    public Blizzard(LivingEntity e) {
        super();
    }

    @Override
    public void spawnDisaster(LivingEntity e, Location loc) {
        Location startLoc = e.getEyeLocation();
        World world = startLoc.getWorld();

        ArrayList<Block> blocks = GeneralPurposeUtil.getBlocksInSquare(startLoc.getBlock(), 30, true);
        Random random = new Random();

        if (world == null)
            return;

        new BukkitRunnable() {

            int counter = 20 * 60 * 2;
            final UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                try {
                    counter--;

                    if (counter <= 0) {
                        cancel();
                        return;
                    }

                    if (counter % 10 == 0) {
                        for (Entity entity : world.getNearbyEntities(startLoc, 80, 80, 80)) {
                            if (!(entity instanceof LivingEntity livingEntity) || entity == e || entity.getType() == EntityType.ARMOR_STAND)
                                continue;

                            livingEntity.damage(15, e);
                        }
                    }

                    for (Entity entity : world.getNearbyEntities(startLoc, 40, 40, 40)) {
                        if (!(entity instanceof LivingEntity livingEntity) || entity == e || entity.getType() == EntityType.ARMOR_STAND)
                            continue;

                        livingEntity.setFreezeTicks(20 * 60);
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld() != loc.getWorld())
                            continue;
                        if (player.getLocation().distance(loc) <= 100)
                            player.spawnParticle(Particle.SNOWFLAKE, startLoc, 500, 25, 25, 25, 0);
                    }
                    for (int i = 0; i < 80; i++) {
                        int temp = random.nextInt(blocks.size());
                        if (blocks.get(temp).getLocation().clone().add(0, 1, 0).getBlock().getType().isSolid())
                            continue;
                        logBlockBreak(uuid, new CustomLocation(blocks.get(temp).getLocation().clone().add(0, 1, 0)));
                        blocks.get(temp).getLocation().clone().add(0, 1, 0).getBlock().setType(Material.SNOW);
                    }
                } catch (Exception e) {
                    ErrorLoggerUtil.logDisaster(e, "Blizzard");
                    cancel();
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                rollbackChanges(uuid);
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 2);
    }

    @Override
    public ItemStack getItem() {
        return GeneralItemsUtil.getLightning();
    }
}
