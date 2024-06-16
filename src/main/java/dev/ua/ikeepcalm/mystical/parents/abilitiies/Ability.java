package dev.ua.ikeepcalm.mystical.parents.abilitiies;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import lombok.Getter;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public abstract class Ability {

    private final CoreProtectAPI coreProtectAPI = LordOfTheMinecraft.coreProtect;
    @Getter
    protected final int identifier;
    @Getter
    protected final Pathway pathway;
    protected Player p;
    @Getter
    protected final int sequence;
    protected final Items items;

    public Ability(int identifier, Pathway pathway, int sequence, Items items) {
        this.identifier = identifier;
        this.pathway = pathway;
        this.sequence = sequence;
        this.items = items;
    }

    public abstract void useAbility();

    public abstract ItemStack getItem();

    public void onHold() {
    }

    public void leftClick() {
    }

    public void removeAbility() {
    }

    public void logBlockBreak(UUID uuid, CustomLocation location) {
        if (coreProtectAPI != null) {
            coreProtectAPI.logRemoval(String.valueOf(uuid), location.toLocation(), location.type(), location.data());
        }
    }

    // Method to rollback changes
    protected void rollbackChanges(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                coreProtectAPI.performRollback(
                        30,
                        List.of(String.valueOf(uuid)),
                        null,
                        null,
                        null,
                        null,
                        0,
                        null);
            }
        }.runTaskLaterAsynchronously(LordOfTheMinecraft.instance, 20 * 20L);
    }

    protected void rollbackChanges(CustomLocation location, int power) {
        power *= 30;
        int finalPower = power;
        new BukkitRunnable() {
            @Override
            public void run() {
                coreProtectAPI.performRollback(
                        30,
                        null,
                        null,
                        null,
                        null,
                        null,
                        finalPower,
                        location.toLocation());
            }
        }.runTaskLaterAsynchronously(LordOfTheMinecraft.instance, 20 * 20L);
    }


    public double getMultiplier() {
        return getMultiplier(pathway);
    }

    public static double getMultiplier(Pathway pathway) {
        double multiplier = 1;
        if (pathway.getSequence().getSequenceMultiplier().containsKey(pathway.getSequence().getCurrentSequence())) {
            multiplier = pathway.getSequence().getSequenceMultiplier().get(pathway.getSequence().getCurrentSequence());
        } else {
            for (int i = pathway.getSequence().getCurrentSequence(); i < 9; i++) {
                if (pathway.getSequence().getSequenceMultiplier().containsKey(i)) {
                    multiplier = pathway.getSequence().getSequenceMultiplier().get(i);
                }
            }
        }
        return multiplier;
    }
}
