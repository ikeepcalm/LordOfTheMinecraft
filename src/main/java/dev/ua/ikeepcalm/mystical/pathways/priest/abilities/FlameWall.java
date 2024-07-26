package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FlameWall extends Ability implements Listener {

    private static final int RADIUS = 10;
    private static final int DURATION = 200; // Duration in ticks (10 seconds)
    private static final int HEIGHT = 5; // Height of the flame wall
    private Location centerLocation;

    public FlameWall(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        Location center = player.getLocation();
        centerLocation = center;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= DURATION) {
                    removeFire(center);
                    centerLocation = null;
                    this.cancel();
                    return;
                }

                if (ticks < HEIGHT * 3) {
                    // Animate the circle building
                    for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
                        placeFire(center, angle, ticks / 2.0);
                    }
                } else {
                    // Maintain the flame wall for the rest of the duration
                    for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
                        for (int y = 0; y < HEIGHT; y++) {
                            placeFire(center, angle, y);
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 1L);
    }

    private void placeFire(Location center, double angle, double y) {
        int x = center.getBlockX() + (int) (RADIUS * Math.cos(angle));
        int z = center.getBlockZ() + (int) (RADIUS * Math.sin(angle));
        Location fireLocation = new Location(center.getWorld(), x + 0.5, center.getBlockY() + y, z + 0.5);

        // Use particles for fire effect
        center.getWorld().spawnParticle(Particle.FLAME, fireLocation, 1, 1, 1, 1, 0);

        // Optionally, set fire blocks for visual effect (remove this if only particles are desired)
        if (y == 0 && fireLocation.getBlock().getType() == Material.AIR) {
            fireLocation.getBlock().setType(Material.FIRE);
        }
    }

    private void removeFire(Location center) {
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            for (int y = 0; y < HEIGHT; y++) {
                int x = center.getBlockX() + (int) (RADIUS * Math.cos(angle));
                int z = center.getBlockZ() + (int) (RADIUS * Math.sin(angle));
                Location fireLocation = new Location(center.getWorld(), x, center.getBlockY() + y, z);
                if (fireLocation.getBlock().getType() == Material.FIRE) {
                    fireLocation.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (centerLocation != null) {
            if (isCrossingBorder(centerLocation, event.getFrom(), event.getTo())) {
                player.damage(4.0 * getMultiplier());
            }
        }
    }

    private boolean isCrossingBorder(Location center, Location from, Location to) {
        double fromDistanceSquared = center.distanceSquared(from);
        double toDistanceSquared = center.distanceSquared(to);
        double radiusSquared = RADIUS * RADIUS;

        return (fromDistanceSquared <= radiusSquared && toDistanceSquared > radiusSquared) ||
               (fromDistanceSquared > radiusSquared && toDistanceSquared <= radiusSquared);
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.LAVA_BUCKET, "Стіна полум'я", "200", identifier);
    }
}
