package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WindManipulation extends Ability {

    private Category selectedCategory = Category.BLADE;
    private final Category[] categories = Category.values();
    private int selected = 0;
    private boolean flying;

    public WindManipulation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        player = pathway.getBeyonder().getPlayer();
        flying = false;
    }

    enum Category {
        BLADE("§9Вітрове Лезо"),
        FLIGHT("§9Політ"),
        BOOST("§9Прискорення"),
        BIND("§9Вітрове Знерухомлення");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        switch (selectedCategory) {
            case BLADE -> blade(player, getMultiplier());
            case BOOST -> boost(player);
            case FLIGHT -> flight(player);
            case BIND -> bind(player);
        }
    }

    private void bind(Entity caster) {
        Vector dir = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);
        if (loc.getWorld() == null)
            return;

        Entity target = null;

        outerloop:
        for (int i = 0; i < 50; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (entity.getType() == EntityType.ARMOR_STAND || entity == caster)
                    continue;
                target = entity;
                break outerloop;
            }

            loc.add(dir);

            if (loc.getBlock().getType().isSolid()) {
                break;
            }
        }

        if (target == null) {
            return;
        }

        Entity finalTarget = target;

        if (pathway.getBeyonder().getSpirituality() <= 25)
            return;

        pathway.getSequence().removeSpirituality(25);

        if (finalTarget instanceof LivingEntity livingEntity) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 25, 8, false, false));
        }

        for (int i = 0; i < 10; i++) {
            new BukkitRunnable() {
                final double spiralRadius = finalTarget.getWidth();

                double spiral = 0;
                double height = 0;
                double spiralX;
                double spiralZ;

                double counter = 20 * 25;

                @Override
                public void run() {
                    try {
                        Location entityLoc = finalTarget.getLocation().clone();
                        entityLoc.add(0, -0.75, 0);

                        counter--;
                        if (counter <= 0) {
                            cancel();
                            return;
                        }

                        spiralX = spiralRadius * Math.cos(spiral);
                        spiralZ = spiralRadius * Math.sin(spiral);
                        spiral += 0.25;
                        height += .025;
                        if (height >= 2.3) {
                            height = 0;
                        }

                        if (entityLoc.getWorld() == null)
                            return;

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getWorld() != entityLoc.getWorld() || player.getLocation().distance(entityLoc) > 100)
                                continue;
                            player.spawnParticle(Particle.ASH, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 5, 0, 0, 0, 0);
                        }
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Wind Manipulation - Bind");
                        cancel();
                    }
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, i * 15, 2);
        }

    }

    private void boost(Entity caster) {
        if (!caster.isOnGround() && !flying) {
            caster.sendMessage("§cСтаньте на землю, щоб використовувати це заклинання!");
            return;
        }

        if (pathway.getBeyonder().getSpirituality() <= 20)
            return;

        pathway.getSequence().removeSpirituality(20);

        caster.setVelocity(caster.getLocation().getDirection().normalize().multiply(5));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() != caster.getWorld() || p.getLocation().distance(caster.getLocation()) > 100)
                continue;

            p.spawnParticle(Particle.CLOUD, caster.getLocation(), 100, .05, .05, .05, .65);
        }
    }

    private void flight(Player caster) {
        flying = !flying;

        if (!flying)
            return;

        boolean allowFlight = caster.getAllowFlight();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (pathway.getBeyonder().getSpirituality() <= 80) {
                        flying = false;
                        cancel();
                        return;
                    }

                    boolean foundWater = false;
                    List<Block> blockList = GeneralPurposeUtil.getBlocksInCircleRadius(caster.getLocation().getBlock(), 8, true, Material.DIRT, Material.STONE, Material.GRASS_BLOCK);
                    for (Block block : blockList) {
                        if (block.getType() == Material.WATER) {
                            foundWater = true;
                            break;
                        }
                    }
                    if (foundWater) {
                        pathway.getSequence().removeSpirituality(8);
                    } else {
                        pathway.getSequence().removeSpirituality(50);
                    }

                    if (!flying)
                        cancel();
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Wind Manipulation - Flight");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20);

        for (int i = 0; i < 12; i++) {
            new BukkitRunnable() {
                double spiralRadius = .1;

                double spiral = 0;
                double height = 0;
                double spiralX;
                double spiralZ;

                @Override
                public void run() {
                    try {
                        Location entityLoc = caster.getLocation().clone();
                        entityLoc.add(0, -0.75, 0);

                        spiralX = spiralRadius * Math.cos(spiral);
                        spiralZ = spiralRadius * Math.sin(spiral);
                        spiral += 0.25;
                        height += .025;
                        spiralRadius += .015;
                        if (height >= 2.3) {
                            height = 0;
                            spiralRadius = .1;
                        }

                        if (entityLoc.getWorld() == null)
                            return;

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getWorld() != entityLoc.getWorld() || player.getLocation().distance(entityLoc) > 100)
                                continue;
                            player.spawnParticle(Particle.ASH, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 5, 0, 0, 0, 0);
                        }

                        caster.setAllowFlight(true);
                        caster.setFlying(true);

                        if (!flying) {
                            caster.setAllowFlight(allowFlight);
                            cancel();
                        }
                    } catch (Exception e) {
                        LoggerUtil.logAbilityError(e, "Wind Manipulation - Flight Particles");
                        cancel();
                    }
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, i * 15, 2);
        }
    }

    private void blade(Entity caster, double multiplier) {
        Vector direction = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);
        World world = loc.getWorld();

        if (world == null)
            return;

        loc.add(direction.clone().multiply(2));

        if (pathway.getBeyonder().getSpirituality() <= 45)
            return;
        pathway.getSequence().removeSpirituality(45);

        world.playSound(loc, Sound.ENTITY_ARROW_SHOOT, 1, 1);

        new BukkitRunnable() {
            int counter = 20;
            final UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                try {
                    Bukkit.getScheduler().runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getWorld() != loc.getWorld() || p.getLocation().distance(loc) > 100)
                                continue;
                            drawBlade(loc, p, direction);
                        }
                    });

                    if (loc.getBlock().getType().isSolid()) {
                        if (loc.getBlock().getType().getHardness() < 0 || loc.getBlock().getType().getHardness() > .7)
                            counter = 0;
                        else {
                            logBlockBreak(uuid, new CustomLocation(loc));
                            loc.getBlock().setType(Material.AIR);
                        }
                    }

                    for (Entity entity : world.getNearbyEntities(loc, 1, 3, 1)) {
                        if (entity instanceof LivingEntity livingEntity && entity != caster && entity.getType() != EntityType.ARMOR_STAND) {
                            livingEntity.damage(12 * multiplier);
                            counter = 0;
                        }
                    }

                    loc.add(direction);

                    counter--;
                    if (counter <= 0) {
                        rollbackChanges(uuid);
                        cancel();
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Wind Manipulation - Blade");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private void drawBlade(Location loc, Player drawPlayer, Vector direction) {
        Vector dir = direction.clone();
        dir.setY(0);
        dir.normalize().multiply(-.5);

        Random random = new Random();

        for (double d = 0; d < 1.75; d += .15) {
            drawPlayer.spawnParticle(Particle.ASH, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.25, d))), 1, 0, 0, 0, 0);
            if (random.nextInt(4) == 0)
                drawPlayer.spawnParticle(Particle.CLOUD, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.5, d))), 1, 0, 0, 0, 0);
        }
        for (double d = 0; d > -1.75; d -= .15) {
            drawPlayer.spawnParticle(Particle.ASH, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.25, d * -1))), 1, 0, 0, 0, 0);
            if (random.nextInt(4) == 0)
                drawPlayer.spawnParticle(Particle.CLOUD, loc.clone().add(0, d, 0).add(dir.clone().multiply(Math.pow(2.5, d))), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.FEATHER, "Володарювання Вітром", "різниться", identifier);
    }

    @Override
    //Cycle through categories on left click
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
    }

    @Override
    //Display selected category
    public void onHold() {
        if (player == null)
            player = pathway.getBeyonder().getPlayer();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обране заклинання: §f" + selectedCategory.name));
    }
}
