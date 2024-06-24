package dev.ua.ikeepcalm.entities.disasters;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Earthquake extends Disaster implements Listener {
    private final ArrayList<FallingBlock> fallingBlocks;

    public Earthquake(LivingEntity p) {
        super(p);
        fallingBlocks = new ArrayList<>();
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void spawnDisaster(LivingEntity p, Location loc) {

        final Location startLoc = loc.clone();
        final World world = startLoc.getWorld();

        if (world == null)
            return;

        final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(110, 38, 14), 3f);
        final Random random = new Random();

        new BukkitRunnable() {
            int counter = 20 * 60;
            UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                counter--;
                if (counter <= 0) {
                    cancel();
                    return;
                }

                for (Entity entity : world.getNearbyEntities(startLoc, 60, 60, 60)) {
                    if (entity == p || !(entity instanceof LivingEntity livingEntity))
                        continue;

                    if (!entity.isOnGround())
                        continue;

                    livingEntity.damage(4, p);
                    if (counter % 15 == 0)
                        livingEntity.setVelocity(new Vector(random.nextDouble(-1, 1), 0, random.nextDouble(-1, 1)));
                }

                for (int j = 0; j < 12; j++) {
                    Block b = startLoc.clone().add(random.nextInt(-35, 35), random.nextInt(-5, 15), random.nextInt(-35, 35)).getBlock();
                    for (int i = 0; i < 55; i++) {
                        Material m = b.getType();

                        if (m == Material.AIR)
                            continue;

                        logBlockBreak(uuid, new CustomLocation(b.getLocation()));
                        b.setType(Material.AIR);

                        FallingBlock fallingBlock = world.spawnFallingBlock(b.getLocation().clone().add(0, .5, 0), m.createBlockData());
                        fallingBlock.setVelocity(new Vector(random.nextDouble(-.1, .1), random.nextDouble(.25, 1), random.nextDouble(-.1, .1)));

                        fallingBlocks.add(fallingBlock);

                        b = b.getLocation().add(new Vector(random.nextInt(-1, 1), 0, random.nextInt(-1, 1))).getBlock();
                    }
                }

                if (counter % 20 == 0)
                    world.spawnParticle(Particle.DUST, startLoc, 200, 35, 0, 35, dust);
            }

            @Override
            public void cancel() {
                super.cancel();
                rollbackChanges(uuid);
                for (FallingBlock fallingBlock : fallingBlocks) {
                    fallingBlock.remove();
                }
            }

        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @EventHandler
    public void onEntityToBlockConvert(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock fallingBlock))
            return;
        if (!fallingBlocks.contains(fallingBlock))
            return;

        fallingBlocks.remove(fallingBlock);
        e.setCancelled(true);
    }

    @Override
    public ItemStack getItem() {
        return null;
    }
}
