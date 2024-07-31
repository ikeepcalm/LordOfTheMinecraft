package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.mobs.*;
import dev.ua.ikeepcalm.mystical.parents.abilities.MobAbility;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RampagerListener implements Listener {
    private final List<MobAbility> abilities = new ArrayList<>();

    public RampagerListener(Warden warden) {
        this.abilities.add(new BaneAbility(1));
        this.abilities.add(new HolyLightSummoning(1));
        this.abilities.add(new PlundererAbility(1));
        this.abilities.add(new RoosterAbility(1));
        this.abilities.add(new SpawnVex(1));
        this.monitorWarden(warden);
    }

    @EventHandler
    public void onRampagerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Warden warden) {
            if (!entity.getMetadata("pathway").isEmpty()) {
                Entity damager = event.getDamager();
                if (damager instanceof Player player) {
                    if (this.shouldPerformSpecialAttack()) {
                        LordOfTheMinecraft.instance.log("Warden is performing special attack");
                        String sequence = entity.getMetadata("sequence").getFirst().asString();
                        MobAbility ability = this.abilities.get((new Random()).nextInt(this.abilities.size()));
                        ability.useAbility(warden.getLocation(), player.getLocation(), this.getMultiplier(GeneralPurposeUtil.parseInt(sequence)), warden, player);
                    }
                }
            }
        }
    }

    public void monitorWarden(Warden warden) {
        new BukkitRunnable() {
            int time = 0;
            int x = warden.getLocation().getBlockX();
            int y = warden.getLocation().getBlockY();
            int z = warden.getLocation().getBlockZ();

            public void run() {
                if (!warden.isValid()) {
                    this.cancel();
                } else {
                    ++this.time;
                    if (this.time == 10) {
                        Location currentLocation = warden.getLocation();
                        if (currentLocation.getBlockX() == this.x && currentLocation.getBlockY() == this.y && currentLocation.getBlockZ() == this.z) {
                            LordOfTheMinecraft.instance.log("Warden is at the same location for 100 ticks");
                            createExplosion(warden);
                        } else {
                            this.x = currentLocation.getBlockX();
                            this.y = currentLocation.getBlockY();
                            this.z = currentLocation.getBlockZ();
                        }
                        this.time = 0;
                    }

                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 20L);
    }

    private void createExplosion(Warden warden) {
        World world = warden.getWorld();
        world.createExplosion(warden.getLocation(), 2.0F, false, true, warden);
    }

    private boolean shouldPerformSpecialAttack() {
        Random random = new Random();
        return random.nextDouble() < 0.2;
    }

    private double getMultiplier(int sequence) {
        double var10000;
        switch (sequence) {
            case 1 -> var10000 = 5.0;
            case 2 -> var10000 = 4.0;
            case 3 -> var10000 = 3.0;
            case 4 -> var10000 = 2.5;
            case 5 -> var10000 = 2.0;
            case 6 -> var10000 = 1.5;
            case 7 -> var10000 = 1.0;
            default -> var10000 = 0.5;
        }

        return var10000;
    }
}
