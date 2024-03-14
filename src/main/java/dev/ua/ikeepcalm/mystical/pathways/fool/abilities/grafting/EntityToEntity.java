package dev.ua.ikeepcalm.mystical.pathways.fool.abilities.grafting;


import dev.ua.ikeepcalm.LordOfTheMinecraft;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class EntityToEntity {

    private boolean stopped;
    @Getter
    private final Entity entity;

    public EntityToEntity(Entity entity, Entity target) {
        this.entity = entity;
        stopped = false;
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;

                if (stopped || counter > 20 * 60 * 60 || entity == null || !entity.isValid() || target == null || !target.isValid()) {
                    cancel();
                    return;
                }

                if (target.getLocation().distance(entity.getLocation()) > 3 || target.getLocation().getWorld() != entity.getWorld()) {
                    entity.teleport(target.getLocation());
                } else {
                    Vector dir = target.getLocation().toVector().subtract(entity.getLocation().toVector());
                    entity.setVelocity(dir);
                }

            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    public void stop() {
        stopped = true;
    }

}