package dev.ua.ikeepcalm.mystical.parents.abilities;

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

    @Getter
    protected final int identifier;
    @Getter
    protected final Pathway pathway;
    @Getter
    protected final int sequence;
    protected final Items items;
    private final CoreProtectAPI coreProtectAPI = LordOfTheMinecraft.coreProtect;
    protected Player player;

    public Ability(int identifier, Pathway pathway, int sequence, Items items) {
        this.identifier = identifier;
        this.pathway = pathway;
        this.sequence = sequence;
        this.items = items;
    }

    public static double getMultiplier(Pathway pathway) {
        double multiplier = 1;
        if (pathway.getNameNormalized().equalsIgnoreCase("sun")) {
            multiplier = 1.8;
        }
        if (pathway.getSequence() == null) {
            return multiplier;
        }
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

    public double getMultiplier() {
        return getMultiplier(pathway);
    }
}
