package dev.ua.ikeepcalm.mystical.pathways.fool.abilities.marionetteAbilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpiritBodyThreads extends Ability implements Listener {

    private boolean controlling;
    private Entity currentEntity;
    private int index;
    private final int[] maxDistance;
    private boolean onlyShowPlayers;

    private final int[] convertTimePerLevel;

    List<Entity> nearbyEntities;
    @Getter
    private final List<Marionette> marionettes;

    private final Particle.DustOptions dustGray, dustWhite, dustPurple, dustBlue;

    public SpiritBodyThreads(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        player = pathway.getBeyonder().getPlayer();
        items.addToSequenceItems(identifier - 1, sequence);

        controlling = false;
        currentEntity = null;
        index = 0;

        onlyShowPlayers = false;

        marionettes = new ArrayList<>();

        dustGray = new Particle.DustOptions(Color.fromRGB(80, 80, 80), .75f);
        dustWhite = new Particle.DustOptions(Color.fromRGB(255, 255, 255), .75f);
        dustPurple = new Particle.DustOptions(Color.fromRGB(221, 0, 255), .75f);
        dustBlue = new Particle.DustOptions(Color.fromRGB(0, 128, 255), .75f);

        convertTimePerLevel = new int[]{
                10,
                16,
                24,
                36,
                40,
                48
        };

        maxDistance = new int[]{
                500,
                200,
                150,
                125,
                75,
                12
        };

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        if (controlling) {
            controlling = false;
            return;
        }

        if (marionettes.stream().anyMatch(marionette -> marionette.getEntity() == currentEntity))
            return;

        if (currentEntity == null) {
            getNearbyEntities();
            if (nearbyEntities.isEmpty()) {
                return;
            }
            if (nearbyEntities.getFirst() instanceof Player player) {
                if (player.getUniqueId().equals(this.player.getUniqueId())) {
                    return;
                }
            }
        }

        ((LivingEntity) currentEntity).damage(1, player);
        currentEntity.setMetadata("isBeingControlled", new FixedMetadataValue(LordOfTheMinecraft.instance, player.getUniqueId()));

        controlling = true;
        int convertTimeSeconds = convertTimePerLevel[pathway.getSequence().getCurrentSequence()];

        if (targetIsBeyonder()) {
            switch (getTargetSequence() - pathway.getSequence().getCurrentSequence()) {
                case -4 -> {
                    player.damage(100, currentEntity);
                    player.getWorld().createExplosion(player.getLocation(), 6, false, false);
                    controlling = false;
                    player.sendMessage(Component.text("Ваша різниця в послідовності занадто велика!").color(NamedTextColor.RED));
                    return;
                }
                case -3 -> {
                    player.damage(60, currentEntity);
                    player.getWorld().createExplosion(player.getLocation(), 4, false, false);
                    controlling = false;
                    player.sendMessage(Component.text("Ваша різниця в послідовності занадто велика!").color(NamedTextColor.RED));
                    return;
                }
                case -2 -> {
                    player.damage(20, currentEntity);
                    player.getWorld().createExplosion(player.getLocation(), 2, false, false);
                    controlling = false;
                    player.sendMessage(Component.text("Ваша різниця в послідовності занадто велика!").color(NamedTextColor.RED));
                    return;
                }
                case -1 -> convertTimeSeconds *= 10;
                case 0 -> convertTimeSeconds *= 5;
                case 1 -> convertTimeSeconds *= 2;
            }

            if (currentEntity instanceof Player playerTarget) {
                playerTarget.damage(0, player);
                playerTarget.sendTitle("§5Нитки...", "§5маріонеточника!!!", 10, 70, 20);
                playerTarget.sendMessage(Component.text("Вас захоплено нитками маріонеточника! У вас є " + convertTimeSeconds + "c щоб розірвати дистанцію або вбити нападника!").color(NamedTextColor.DARK_PURPLE));
            }
        }

        startControlling(convertTimeSeconds);
        drawSpiralAroundTarget(convertTimeSeconds);
    }

    private boolean targetIsBeyonder() {
        return LordOfTheMinecraft.beyonders.containsKey(currentEntity.getUniqueId());
    }

    private int getTargetSequence() {
        if (LordOfTheMinecraft.beyonders.containsKey(currentEntity.getUniqueId())) {
            return LordOfTheMinecraft.beyonders.get(currentEntity.getUniqueId()).getPathway().getSequence().getCurrentSequence();
        } else return -1;
    }

    private void startControlling(int convertTimeSeconds) {
        new BukkitRunnable() {
            int counter = convertTimeSeconds * 20;

            @Override
            public void run() {
                LordOfTheMinecraft.instance.log("Controlling tick");
                int currentSequence = pathway.getSequence().getCurrentSequence();
                if (!player.isValid() || !currentEntity.isValid() || !controlling || countDistance(player.getLocation(), currentEntity.getLocation()) > maxDistance[currentSequence]) {
                    controlling = false;
                    if (currentEntity != null && currentEntity.isValid()) {
                        currentEntity.removeMetadata("isBeingControlled", LordOfTheMinecraft.instance);
                        currentEntity.sendMessage(Component.text("Вам вдалося узяти контроль над собою...").color(NamedTextColor.DARK_PURPLE));
                    }
                    cancel();
                    return;
                }

                if (currentEntity instanceof Player playerTarget) {
                    playerTarget.sendActionBar(Component.text("До конвертації у маріонетку - " + this.counter / 20 + "c").color(NamedTextColor.DARK_PURPLE));
                }

                drawLineToEntity(player.getEyeLocation(), currentEntity.getLocation().add(0, .5, 0), dustPurple);
                giveEffectsToTarget(counter);

                counter--;

                if (counter <= 0) {
                    controlling = false;
                    turnIntoMarionette();
                    cancel();
                }
            }

        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    private int countDistance(Location loc1, Location loc2) {
        return (int) Math.round(loc1.distance(loc2));
    }

    private void turnIntoMarionette() {
        if (currentEntity == null || !currentEntity.isValid())
            return;

        boolean isBeyonder = targetIsBeyonder();

        int pathway = -1;
        int sequence = -1;
        String name = currentEntity.getName();

        if (isBeyonder) {
            if (LordOfTheMinecraft.beyonders.containsKey(currentEntity.getUniqueId())) {
                pathway = LordOfTheMinecraft.beyonders.get(currentEntity.getUniqueId()).getPathway().getPathwayInt();
                sequence = LordOfTheMinecraft.beyonders.get(currentEntity.getUniqueId()).getPathway().getSequence().getCurrentSequence();
            }
        }

        Marionette marionette = new Marionette(
                isBeyonder,
                sequence,
                pathway,
                currentEntity.getType(),
                player.getUniqueId(),
                currentEntity.getLocation(),
                name,
                this,
                Objects.requireNonNull(((LivingEntity) currentEntity).getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue()
        );

        marionettes.add(marionette);

        if (CitizensAPI.getNPCRegistry().isNPC(currentEntity))
            CitizensAPI.getNPCRegistry().getNPC(currentEntity).destroy();
        else if (!(currentEntity instanceof Player playerTarget))
            currentEntity.remove();
        else
            playerTarget.setHealth(0);
    }

    private void drawSpiralAroundTarget(int convertTimeSeconds) {
        new BukkitRunnable() {
            long counter = 10L * convertTimeSeconds;
            double spiralRadius = 2;

            double spiral = 0;
            double height = 0;
            double spiralX;
            double spiralZ;

            @Override
            public void run() {
                Location entityLoc = currentEntity.getLocation().clone();
                entityLoc.add(0, 0.75, 0);

                spiralX = spiralRadius * Math.cos(spiral);
                spiralZ = spiralRadius * Math.sin(spiral);
                spiral += 0.25;
                height += .05;
                if (height >= 2.5)
                    height = 0;
                if (entityLoc.getWorld() != null)
                    entityLoc.getWorld().spawnParticle(Particle.DUST, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 5, dustPurple);

                counter--;
                spiralRadius -= (1.5 / (10L * convertTimeSeconds));

                if (!controlling)
                    cancel();
                if (counter <= 0) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 2);
    }

    private void giveEffectsToTarget(int progress) {
        int multiplier = (int) (Math.round(8d / progress) * 1.5);
        ((LivingEntity) currentEntity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, multiplier));
    }

    @Override
    public void onHold() {
        int currentSequence = pathway.getSequence().getCurrentSequence();

        if (player == null || nearbyEntities == null || currentEntity == null || controlling)
            return;

        Location startLoc = player.getEyeLocation();

        if (!currentEntity.isValid()) {
            index = 0;
            getNearbyEntities();
        }

        if (currentEntity.getWorld() != startLoc.getWorld() || currentEntity.getLocation().distance(startLoc) > maxDistance[currentSequence]) {
            nearbyEntities.remove(currentEntity);
            if (nearbyEntities.isEmpty())
                currentEntity = null;
            else {
                index = 0;
                currentEntity = nearbyEntities.getFirst();
            }
        }

        nearbyEntities.removeIf(entity -> entity.getWorld() != startLoc.getWorld() || entity.getLocation().distance(startLoc) > maxDistance[currentSequence]);

        if (nearbyEntities.isEmpty())
            return;

        for (Entity entity : nearbyEntities) {
            if (entity == player)
                continue;

            if (marionettes.stream().anyMatch(marionette -> marionette.getEntity() == entity))
                drawLineToEntity(startLoc, entity.getLocation().add(0, .5, 0), dustBlue);
            else if (entity == currentEntity)
                drawLineToEntity(startLoc, entity.getLocation().add(0, .5, 0), dustWhite);
            else
                drawLineToEntity(startLoc, entity.getLocation().add(0, .5, 0), dustGray);
        }

        String name = currentEntity.getName();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обрана сутність: §8" + name + " §r-- §5Відстань: §8" + Math.round(currentEntity.getLocation().distance(player.getLocation()))));
    }

    @Override
    public void leftClick() {
        if (player.isSneaking()) {
            onlyShowPlayers = !onlyShowPlayers;
            getNearbyEntities();
            index = 0;
            return;
        }

        do {
            index++;
            if (index >= nearbyEntities.size()) {
                index = 0;
                getNearbyEntities();
            }
        } while (marionettes.stream().anyMatch(marionette -> marionette.getEntity() == nearbyEntities.get(index)));

        currentEntity = nearbyEntities.get(index);
        controlling = false;
    }

    private void drawLineToEntity(Location startLoc, Location target, Particle.DustOptions dust) {
        Location loc = startLoc.clone();
        Vector dir = target
                .toVector()
                .subtract(loc.toVector())
                .normalize()
                .multiply(.75);

        for (int i = 0; i < target.distance(startLoc); i++) {
            player.spawnParticle(
                    Particle.DUST,
                    loc,
                    1,
                    0,
                    0,
                    0,
                    dust
            );
            loc.add(dir);
        }
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.COAL, "Маріонетковий Ткач", "100", identifier);
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent e) {
        if (!e.getPlayer().getName().equals(player.getName())) {
            return;
        } else {
            player = e.getPlayer();
        }

        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item == null || !item.isSimilar(getItem())) {
            return;
        }

        getNearbyEntities();
    }

    private void getNearbyEntities() {
        int currentSequence = pathway.getSequence().getCurrentSequence();
        int distance = maxDistance[currentSequence];
        if (!onlyShowPlayers && distance > 75)
            distance = 75;

        nearbyEntities = player.getNearbyEntities(distance, distance, distance)
                .stream()
                .filter(entity -> entity instanceof LivingEntity && !(entity instanceof ArmorStand))
                .sorted(Comparator.comparing(
                        entity -> entity.getLocation().distance(player.getEyeLocation())))
                .filter(entity -> (!onlyShowPlayers || entity.getType() == EntityType.PLAYER))
                .collect(Collectors.toList());

        if (nearbyEntities.isEmpty())
            return;

        index = 0;
        currentEntity = nearbyEntities.getFirst();
    }

    public void removeMarionette(Marionette marionette) {
        marionettes.remove(marionette);
    }
}
