package dev.ua.ikeepcalm.mystical.pathways.fool.abilities.grafting;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import lombok.Getter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class HealthSynchronization implements Listener {

    @Getter
    private Entity entity1;
    @Getter
    private Entity entity2;

    private double maxHealth;

    private boolean stopped;

    public HealthSynchronization(Entity entity1, Entity entity2) {
        this.entity1 = entity1;
        this.entity2 = entity2;

        stopped = false;

        if (!(entity1 instanceof LivingEntity mob1) || !(entity2 instanceof LivingEntity mob2))
            return;

        maxHealth = Objects.requireNonNull(mob1.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        Objects.requireNonNull(mob1.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(mob2.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                Objects.requireNonNull(mob1.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(mob2.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());

                if (mob2.getHealth() <= 0) {
                    reset();
                    cancel();
                    mob1.setHealth(0);
                    return;
                }

                mob1.setHealth(mob2.getHealth());

                if (counter >= 20 * 60 * 60 * 2 || !entity1.isValid() || !entity2.isValid() || stopped) {
                    reset();
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    boolean hasFired = false;

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (entity1 == null | entity2 == null)
            return;

        if (!entity1.isValid() || !entity2.isValid()) {
            reset();
            return;
        }

        if (e.getEntity() != entity1 && e.getEntity() != entity2)
            return;

        if (!(entity1 instanceof LivingEntity mob1))
            return;

        if (e.getEntity() == entity1) {
            e.setCancelled(true);
            return;
        }

        if (hasFired)
            return;

        if (e.getEntity() == entity2) {
            mob1.damage(0);
        }

        hasFired = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                hasFired = false;
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 10);
    }

    private void reset() {
        if (!(entity1 instanceof LivingEntity mob))
            return;

        if (mob.getHealth() > maxHealth)
            mob.setHealth(maxHealth);

        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(maxHealth);

        entity2 = null;
        entity1 = null;
    }

    public void stop() {
        stopped = true;
    }
}
