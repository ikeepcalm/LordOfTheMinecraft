package dev.ua.ikeepcalm.mystical.pathways.priest;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.abilities.*;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PriestItems extends Items {

    public PriestItems(Pathway pathway) {
        super(pathway);
        items = new ArrayList<>();
        abilityInfo = new HashMap<>();
        sequenceItems = new HashMap<>();
        initializeAbilityInfos();
        createItems();
    }

    private void initializeAbilityInfos() {
        HashMap<Integer, String> names = Objects.requireNonNull(Pathway.getNamesForPathway(pathway.getNameNormalized()));
        String[] s9 = formatAbilityInfo(pathway.getStringColor(), "9: " + names.get(9),
                LocalizationUtil.getLocalizedString("priest", "abilities", "trap-master"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "tracking")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("priest", "abilities", "provoke")
        );
        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("priest", "abilities", "fire-raven"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "blazing-spear"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "wall-of-fire"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "fire-armor")
        );
        abilityInfo.put(7, s7);
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

    private void createItems() {
        addAbility(new Trap(1, pathway, 9, this));
        addAbility(new Tracking(2, pathway, 9, this));
        addAbility(new Provoke(3, pathway, 8, this));
        addAbility(new FireRaven(4, pathway, 7, this));
        addAbility(new BlazingSpear(5, pathway, 7, this));
        addAbility(new FlameArmor(6, pathway, 7, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
