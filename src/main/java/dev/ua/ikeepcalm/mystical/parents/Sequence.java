package dev.ua.ikeepcalm.mystical.parents;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.*;

@Getter
public abstract class Sequence {

    private static final Logger log = LoggerFactory.getLogger(Sequence.class);
    @Setter
    protected int currentSequence;
    @Setter
    protected Pathway pathway;
    protected boolean[] usesAbilities;
    @Setter
    protected ArrayList<Ability> abilities;
    protected HashMap<Integer, PotionEffect[]> sequenceEffects;
    protected HashMap<Integer, PotionEffectType[]> sequenceResistances;
    protected HashMap<Integer, Double> sequenceMultiplier;
    protected Set<UUID> subordinates = new HashSet<>();
    protected int[] maxSubordinates = new int[]{9999, 20, 15, 10, 5};
    private LocalTime lastTime;


    public Sequence(Pathway pathway, int optionalSequence) {
        this.pathway = pathway;
        this.currentSequence = optionalSequence;
    }

    public void useAbility(ItemStack item, PlayerInteractEvent e) {
        if (!checkValid(item))
            return;

        e.setCancelled(true);
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
            useAbility(Objects.requireNonNull(item.getItemMeta()).getEnchantLevel(Enchantment.CHANNELING), item);
            return;
        }

        int id = Objects.requireNonNull(item.getItemMeta()).getEnchantLevel(Enchantment.CHANNELING);
        for (Ability a : abilities) {
            if (a.getIdentifier() == id) {
                a.leftClick();
                break;
            }
        }
    }

    public void destroyItem(ItemStack item, PlayerDropItemEvent e) {
        if (pathway.getItems().getItems().contains(item)) {
            e.getItemDrop().remove();
        }

        for (ItemStack itemStack : GeneralItemsUtil.returnAllItems()) {
            if (itemStack.isSimilar(item))
                e.getItemDrop().remove();
        }
    }

    public void useAbility(int ability, ItemStack item) {

        String spiritualityDrainageStr = NBT.get(item, (nbt) -> {
            return nbt.getString("spiritualityDrainage");
        });

        int spiritualityDrainage;

        try {
            spiritualityDrainage = Integer.parseInt(spiritualityDrainageStr);
        } catch (NumberFormatException e) {
            spiritualityDrainage = 0;
        }

        if (spiritualityDrainage > pathway.getBeyonder().getSpirituality())
            return;

        if (usesAbilities[ability - 1]) {
            if (getIds().contains(ability)) {
                usesAbilities[ability - 1] = false;
            }
            return;
        }

        //remove spirituality
        removeSpirituality(spiritualityDrainage);

        for (Ability a : abilities) {
            if (a.getIdentifier() == ability) {
                a.useAbility();
                Beyonder beyonder = pathway.getBeyonder();
                Player player = beyonder.getPlayer();
                LoggerUtil.logPlayerAbility(player, a.getClass().getSimpleName(), (int) beyonder.getSpirituality(), (int) beyonder.getMaxSpirituality());
                LoggerUtil.logCoreProtect(player, a.getClass().getSimpleName(), player.getLocation().subtract(0, 1, 0));
                if (getIds().contains(ability)) {
                    if (lastTime == null) {
                        lastTime = LocalTime.now();
                        pathway.getBeyonder().updateActing(pathway.getItems().getSequenceOfAbility(a));
                    } else {
                        if (LocalTime.now().isAfter(lastTime.plusSeconds(30))) {
                            lastTime = LocalTime.now();
                            pathway.getBeyonder().updateActing(pathway.getItems().getSequenceOfAbility(a));
                        }
                    }
                } else {
                    pathway.getBeyonder().updateActing(pathway.getItems().getSequenceOfAbility(a));
                }
                break;
            }
        }
    }

    public abstract List<Integer> getIds();

    public boolean checkValid(ItemStack item) {
        if (item == null)
            return false;
        ItemStack checkItem = item.clone();
        checkItem.setAmount(1);

        return pathway.getItems().returnItemsFromSequence(currentSequence).contains(checkItem);
    }


    public void addSpirituality(double add) {
        pathway.getBeyonder().setSpirituality(pathway.getBeyonder().getSpirituality() + add);
    }

    public void removeSpirituality(double remove) {
        pathway.getBeyonder().setSpirituality(pathway.getBeyonder().getSpirituality() - remove);
    }

    public void onHold(ItemStack item) {
        if (!checkValid(item))
            return;

        int id = Objects.requireNonNull(item.getItemMeta()).getEnchantLevel(Enchantment.CHANNELING);
        for (Ability a : abilities) {
            if (a.getIdentifier() == id) {
                a.onHold();
                break;
            }
        }
    }

    public void run() {

    }

}
