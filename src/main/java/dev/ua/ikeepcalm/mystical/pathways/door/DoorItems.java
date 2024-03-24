package dev.ua.ikeepcalm.mystical.pathways.door;

import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.abilities.Record;
import dev.ua.ikeepcalm.mystical.pathways.door.abilities.*;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DoorItems extends Items {

    public DoorItems(Pathway pathway) {
        super(pathway);
        items = new ArrayList<>();

        abilityInfo = new HashMap<>();
        sequenceItems = new HashMap<>();
        initializeAbilityInfos();
        createItems();
    }

    @Override
    public void initializeAbilityInfos() {
        HashMap<Integer, String> names = Objects.requireNonNull(Pathway.getNamesForPathway(pathway.getNameNormalized()));
        String[] s9 = formatAbilityInfo(pathway.getStringColor(), "9: " + names.get(9),
                LocalizationUtil.getLocalizedString("door", "abilities", "door-opening")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("door", "abilities", "fog"),
                LocalizationUtil.getLocalizedString("door", "abilities", "wind"),
                LocalizationUtil.getLocalizedString("door", "abilities", "flash"),
                LocalizationUtil.getLocalizedString("door", "abilities", "freeze"),
                LocalizationUtil.getLocalizedString("door", "abilities", "electric-shock")

        );
        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("door", "abilities", "divination")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("door", "abilities", "record")
        );
        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("door", "abilities", "blink"),
                LocalizationUtil.getLocalizedString("door", "abilities", "travelers-door"),
                LocalizationUtil.getLocalizedString("door", "miscellaneous", "td-hint")
        );
        abilityInfo.put(5, s5);

        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
                LocalizationUtil.getLocalizedString("door", "abilities", "exile"),
                LocalizationUtil.getLocalizedString("door", "abilities", "space-concealment"),
                LocalizationUtil.getLocalizedString("door", "miscellaneous", "sc-hint1"),
                LocalizationUtil.getLocalizedString("door", "miscellaneous", "sc-hint2"),
                LocalizationUtil.getLocalizedString("door", "abilities", "dimensional-pocket")
        );
        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("door", "abilities", "wandering"),
                LocalizationUtil.getLocalizedString("door", "abilities", "conceptualization"),
                LocalizationUtil.getLocalizedString("door", "abilities", "conceptualize")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("door", "abilities", "black-hole"),
                LocalizationUtil.getLocalizedString("door", "abilities", "space-swapping"),
                LocalizationUtil.getLocalizedString("door", "miscellaneous", "ss-hint")
        );
        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("door", "abilities", "starfall"),
                LocalizationUtil.getLocalizedString("door", "abilities", "find-player")
        );
        abilityInfo.put(1, s1);
    }

    @Override
    public ArrayList<ItemStack> returnItemsFromSequence(int sequence) {
        ArrayList<ItemStack> itemsForSequence = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : sequenceItems.entrySet()) {
            if (entry.getValue() >= sequence) {
                itemsForSequence.add(items.get(entry.getKey()));
            }
        }
        return itemsForSequence;
    }

    @Override
    public void createItems() {
        addAbility(new DoorOpening(1, pathway, 9, this));
        addAbility(new Fog(2, pathway, 8, this));
        addAbility(new Wind(3, pathway, 8, this, false));
        addAbility(new Flash(4, pathway, 8, this));
        addAbility(new Freezing(5, pathway, 8, this, false));
        addAbility(new ElectricShock(6, pathway, 8, this, false));
        addAbility(new Divine(7, pathway, 7, this));
        addAbility(new Record(8, pathway, 6, this, false));
        addAbility(new Blink(9, pathway, 5, this));
        addAbility(new TravelersDoor(10, pathway, 5, this));
        addAbility(new Exile(11, pathway, 4, this, false));
        addAbility(new SpaceConcealment(12, pathway, 4, this, false));
        addAbility(new DimensionalPocket(13, pathway, 4, this));
        addAbility(new Wandering(14, pathway, 3, this));
        addAbility(new Conceptualization(15, pathway, 3, this));
        addAbility(new Conceptualize(16, pathway, 3, this, false));
        addAbility(new BlackHole(17, pathway, 2, this, false));
        addAbility(new SpaceSwapping(18, pathway, 2, this));
        addAbility(new StarFall(19, pathway, 1, this));
        addAbility(new FindPlayer(20, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
