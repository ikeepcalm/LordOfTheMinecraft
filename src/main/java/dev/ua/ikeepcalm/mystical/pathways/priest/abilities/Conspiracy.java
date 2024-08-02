package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Conspiracy extends Ability implements Listener {

    private final List<LivingEntity> spawnedMobs = new ArrayList<>();
    private final int DURATION = 100;
    private final int NUM_MOBS = 5;
    private Creeper specialMob;
    private Player targetPlayer;
    private boolean correctMobAttacked = false;

    public Conspiracy(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        targetPlayer = getTargetPlayer(player);
        if (targetPlayer != null) {
            spawnMobsAroundTarget(targetPlayer);
            moveMobsTowardsTarget(targetPlayer);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!correctMobAttacked) {
                        applyDebuffs(targetPlayer);
                    }
                    removeMobs();
                }
            }.runTaskLater(LordOfTheMinecraft.instance, DURATION);
        }
    }

    private Player getTargetPlayer(Player player) {
        for (Entity entity : Objects.requireNonNull(player.getWorld()).getNearbyEntities(player.getEyeLocation(), 5, 5, 5)) {
            if (entity instanceof LivingEntity livingEntity) {
                if (entity instanceof Player target && entity != player) {
                    return target;
                }
            }
        }
        return null;
    }

    private void spawnMobsAroundTarget(Player target) {
        Location center = target.getLocation();
        for (int i = 0; i < NUM_MOBS; i++) {
            Location spawnLocation = getRandomLocationAround(center, 6);
            Zombie zombie = (Zombie) target.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
            setupMob(zombie);
            spawnedMobs.add(zombie);
        }
        Location specialLocation = getRandomLocationAround(center, 6);
        specialMob = (Creeper) target.getWorld().spawnEntity(specialLocation, EntityType.CREEPER);
        setupMob(specialMob);
        spawnedMobs.add(specialMob);
    }

    private Location getRandomLocationAround(Location center, int radius) {
        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double x = center.getX() + radius * Math.cos(angle);
        double z = center.getZ() + radius * Math.sin(angle);
        return new Location(center.getWorld(), x, center.getY(), z);
    }

    private void setupMob(LivingEntity mob) {
        mob.setInvisible(false);
        mob.setSilent(true);
        mob.setAI(true);
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.3);
        mob.setCustomNameVisible(false);
    }

    private void moveMobsTowardsTarget(Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnedMobs.isEmpty()) {
                    this.cancel();
                }
                for (LivingEntity mob : spawnedMobs) {
                    if (mob instanceof Monster monster) {
                        monster.setTarget(target);
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 20L); // Update target every second
    }

    private void applyDebuffs(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
        player.damage(5.0 * getMultiplier());
        player.sendTitle("§cЗмова!", "§4Ви програли у битві розумів!", 10, 40, 10);
    }

    private void removeMobs() {
        for (LivingEntity mob : spawnedMobs) {
            mob.remove();
        }
        spawnedMobs.clear();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof LivingEntity entity) {
            if (spawnedMobs.contains(entity)) {
                if (entity.equals(specialMob)) {
                    correctMobAttacked = true;
                    attacker.sendTitle("§aВідмінно!", "§aВи виплуталися із павутиння!", 10, 40, 10);
                    attacker.setSaturation(20);
                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1));
                    removeMobs();
                } else {
                    applyDebuffs(targetPlayer);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && spawnedMobs.contains(entity)) {
            entity.remove();
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.DEAD_BUSH, "Змова", "400", identifier);
    }
}
