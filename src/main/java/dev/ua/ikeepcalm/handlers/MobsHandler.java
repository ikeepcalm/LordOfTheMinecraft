package dev.ua.ikeepcalm.handlers;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomEntity;
import dev.ua.ikeepcalm.entities.mobs.*;
import dev.ua.ikeepcalm.mystical.parents.abilities.MobAbility;
import dev.ua.ikeepcalm.utils.BeyonderItemsUtil;
import dev.ua.ikeepcalm.utils.BeyonderMobUtil;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

@Getter
public class MobsHandler implements Listener {

    private final ArrayList<CustomEntity> customEntities;

    private final Random random = new Random();
    private boolean isRedMoon = false;
    private boolean onceCheck = false;

    public MobsHandler() {
        customEntities = new ArrayList<>();
        BeyonderMobUtil beyonderMobUtil = new BeyonderMobUtil();
        spawnEntity("§9Лавовий Кальмар", "squid", 20, BeyonderItemsUtil.getLavosSquidBlood(), EntityType.SQUID, 20, null, null, "none", true);
        spawnEntity("§4Глибоководний Марлін", "marlin", 20, BeyonderItemsUtil.getMarlinBlood(), EntityType.SQUID, 20, null, null, "none", true);
        spawnEntity("§7Сіра Гірська Коза Горнакіс", "goat", 20, BeyonderItemsUtil.getGoatHorn(), EntityType.GOAT, 30, null, null, "none", true);
        spawnEntity("§0Чорнапляма Пантера", "panther", 48, BeyonderItemsUtil.getPanther(), EntityType.OCELOT, 60, null, null, "none", true);
        spawnEntity("§5Тисячоликий Мисливець", "thousand-faced", 55, BeyonderItemsUtil.getPituitaryGland(), EntityType.PIGLIN_BRUTE, 70, beyonderMobUtil, EntityType.ILLUSIONER, "none", true);
        spawnEntity("§0Людиноподібна Тінь", "shadow", 100, BeyonderItemsUtil.getShadowCharacteristic(), EntityType.ENDERMAN, 70, beyonderMobUtil, null, "none", true);
        spawnEntity("§7Древній Рейф", "wraith", 100, BeyonderItemsUtil.getWraithDust(), EntityType.SKELETON, 145, beyonderMobUtil, EntityType.VEX, "wraith", true);
        spawnEntity("§5Шестикрила Горгулья", "gargoyle", 120, BeyonderItemsUtil.getGargoyleCrystal(), EntityType.ZOMBIE, 250, beyonderMobUtil, EntityType.IRON_GOLEM, "gargoyle", true);
        spawnEntity("§5Перевертень", "bane", 60, BeyonderItemsUtil.getBizarroEye(), EntityType.WITCH, 200, beyonderMobUtil, EntityType.ALLAY, "bane", true, new BaneAbility(20));
        spawnEntity("§5Розкрадач Духовного Світу", "plunderer", 180, BeyonderItemsUtil.getPlundererBody(), EntityType.ZOMBIFIED_PIGLIN, 250, beyonderMobUtil, EntityType.VEX, "plunderer", false, new SpawnVex(70), new PlundererAbility(35));
        spawnEntity("§5Пес Фулгріма", "wolf", 70, BeyonderItemsUtil.getWolfEye(), EntityType.WOLF, 750, beyonderMobUtil, null, "wolf", true);
        spawnEntity("§5Демонічний Вовк Завіси", "fog-wolf", 160, BeyonderItemsUtil.getWolfHeart(), EntityType.FOX, 750, beyonderMobUtil, EntityType.WOLF, "fog-wolf", true);
        spawnEntity("§4Магмовий Титан", "magma-titan", 60, BeyonderItemsUtil.getMagmaHeart(), EntityType.MAGMA_CUBE, 32, null, null, "none", true);
        spawnEntity("§6Світанковий Півень", "rooster", 85, BeyonderItemsUtil.getRedRoosterComb(), EntityType.CHICKEN, 60, beyonderMobUtil, null, "rooster", true, new RoosterAbility(60));
        spawnEntity("§6Божественний Птах", "divine-bird", 185, BeyonderItemsUtil.getTailFeather(), EntityType.COW, 120, beyonderMobUtil, EntityType.PARROT, "divine-bird", true, new HolyLightSummoning(90), new FlaringSun(350));
        spawnEntity("§6Духовний Птах Договору", "pact-bird", 140, BeyonderItemsUtil.getBirdFeather(), EntityType.PIG, 160, beyonderMobUtil, EntityType.PARROT, "divine-bird", true, new HolyLightSummoning(90), new FlaringSun(350));
        spawnEntity("§bПожирач Духів", "eater", 120, BeyonderItemsUtil.getSpiritPouch(), EntityType.ZOMBIFIED_PIGLIN, 30, beyonderMobUtil, EntityType.ALLAY, "eater", true);
        startNightTracker();
    }

    private void startNightTracker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                        long time = world.getTime();
                        if (time >= 13000 && time < 14000) {
                            if (!isRedMoon) {
                                if (!onceCheck) {
                                    int chance = random.nextInt(100);
                                    isRedMoon = chance < 10;
                                    if (isRedMoon) {
                                        Bukkit.broadcast(Component.text("Багрянцевий Місяць здіймається на світом...").color(NamedTextColor.DARK_RED));
                                    }
                                    onceCheck = true;
                                }
                            }
                        } else if (time >= 23000 || time < 1000) {
                            if (isRedMoon) {
                                isRedMoon = false;
                                Bukkit.broadcast(Component.text("Багрянцевий Місяць заходить...").color(NamedTextColor.DARK_RED));
                            }
                            onceCheck = false;
                        }
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 20L);
    }

    public void toggleRedMoon() {
        isRedMoon = !isRedMoon;
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            if (isRedMoon) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeepSleep(PlayerDeepSleepEvent event) {
        if (isRedMoon) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            player.damage(4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
            player.sendTitle("§cБезсоння", "§4Багрянцевий Місяць не дає вам заснути", 10, 40, 10);
        }

    }

    public boolean getRedMoon() {
        return isRedMoon;
    }

    private void spawnEntity(String name, String id, int rarity, ItemStack drop, EntityType entityType, Integer health, BeyonderMobUtil beyonderMobUtil, EntityType spawnType, String particle, boolean repeatingParticles, MobAbility... abilities) {
        customEntities.add(new CustomEntity(name, id, rarity, drop, entityType, health, beyonderMobUtil, spawnType, particle, repeatingParticles, abilities));
    }

    public boolean spawnEntity(String id, Location location, World world) {
        for (CustomEntity customEntity : customEntities) {
            if (!customEntity.id().equalsIgnoreCase(id)) continue;

            Entity entity = customEntity.spawnType() == null ? world.spawnEntity(location, customEntity.entityType()) : world.spawnEntity(location, customEntity.spawnType());
            entity.setCustomName(customEntity.name());

            if (entity instanceof LivingEntity livingEntity && customEntity.maxHealth() != null) {
                Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(customEntity.maxHealth());
                livingEntity.setHealth(customEntity.maxHealth());

                if (livingEntity.getType() == EntityType.WOLF || livingEntity.getType() == EntityType.FOX) {
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getBaseValue() * 10);
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue() * 6);
                }
            }

            entity.setMetadata("customEntityId", new FixedMetadataValue(LordOfTheMinecraft.instance, customEntity.id()));

            if (customEntity.beyonderMobUtil() != null) {
                customEntity.beyonderMobUtil().addMob(entity, customEntity);
            }
            return true;
        }
        return false;
    }

    public void spawnRandomEntity(Location location, Location velocityLocation, World world) {
        CustomEntity customEntity = customEntities.get(random.nextInt(customEntities.size()));
        Entity entity = customEntity.spawnType() == null ? world.spawnEntity(location, customEntity.entityType()) : world.spawnEntity(location, customEntity.spawnType());
        entity.setCustomName(customEntity.name());

        if (entity instanceof LivingEntity livingEntity && customEntity.maxHealth() != null) {
            Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(customEntity.maxHealth());
            livingEntity.setHealth(customEntity.maxHealth());
        }

        entity.setMetadata("customEntityId", new FixedMetadataValue(LordOfTheMinecraft.instance, customEntity.id()));

        if (customEntity.beyonderMobUtil() != null) {
            customEntity.beyonderMobUtil().addMob(entity, customEntity);
        }

        entity.setVelocity(velocityLocation.getDirection().multiply(0.5));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (isRedMoon) {
            for (CustomEntity customEntity : customEntities) {
                if (e.getEntity().getType() != customEntity.entityType()) continue;

                Random random = new Random();
                if (random.nextInt(customEntity.rarity()) != 0) return;

                Entity entity;
                if (customEntity.spawnType() == null) entity = e.getEntity();
                else {
                    entity = e.getEntity().getWorld().spawnEntity(e.getLocation(), customEntity.spawnType());
                    e.getEntity().remove();
                }

                entity.setCustomName(customEntity.name());

                if (entity instanceof LivingEntity livingEntity && customEntity.maxHealth() != null) {
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(customEntity.maxHealth());
                    livingEntity.setHealth(customEntity.maxHealth());
                }

                entity.setMetadata("customEntityId", new FixedMetadataValue(LordOfTheMinecraft.instance, customEntity.id()));

                if (customEntity.beyonderMobUtil() != null) {
                    customEntity.beyonderMobUtil().addMob(entity, customEntity);
                }

                LordOfTheMinecraft.instance.log("§c" + customEntity.name() + "§r spawned due to red moon");
            }
        }
    }

    @EventHandler
    public void onEntityDie(EntityDamageEvent e) {
        if (e.getEntity().getMetadata("customEntityId").isEmpty()) return;

        if (!(e.getEntity() instanceof LivingEntity livingEntity)) return;

        if (livingEntity.getHealth() > e.getDamage()) return;

        for (CustomEntity customEntity : customEntities) {
            if (Objects.equals(e.getEntity().getMetadata("customEntityId").getFirst().value(), customEntity.id())) {
                livingEntity.getWorld().dropItem(livingEntity.getLocation(), customEntity.drop());
                break;
            }
        }
    }


}
