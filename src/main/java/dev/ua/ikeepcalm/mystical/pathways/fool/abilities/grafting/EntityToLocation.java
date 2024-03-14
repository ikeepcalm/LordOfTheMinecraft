package dev.ua.ikeepcalm.mystical.pathways.fool.abilities.grafting;


import dev.ua.ikeepcalm.entities.beyonders.Beyonder;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class EntityToLocation {

    private boolean stopped;
    @Getter
    private final Entity entity;

    public EntityToLocation(Entity entity, Location location) {
        this.entity = entity;

        int maxCounter = 20 * 60 * 5;

        if (entity == null)
            return;

        int[] timeBySequence = new int[]{
                0, 20 * 7, 20 * 20, 20 * 40, 20 * 60, 20 * 60 * 2, 20 * 60 * 3, 20 * 60 * 4
        };

        if (LordOfTheMinecraft.beyonders.containsKey(entity.getUniqueId())) {
            Beyonder beyonderTarget = LordOfTheMinecraft.beyonders.get(entity.getUniqueId());
            if (beyonderTarget.getPathway() != null && beyonderTarget.getPathway().getSequence() != null && beyonderTarget.getPathway().getSequence().getCurrentSequence() < 8)
                maxCounter = timeBySequence[beyonderTarget.getPathway().getSequence().getCurrentSequence()];
        }

        stopped = false;
        int finalMaxCounter = maxCounter;
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                if (stopped || counter > finalMaxCounter || location == null || !entity.isValid()) {
                    cancel();
                    return;
                }

                if (location.distance(entity.getLocation()) > 3 || location.getWorld() != entity.getWorld()) {
                    entity.teleport(location);
                } else {
                    Vector dir = location.toVector().subtract(entity.getLocation().toVector());
                    entity.setVelocity(dir);
                }

            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    public void stop() {
        stopped = true;
    }

}