package dev.ua.ikeepcalm.mystical.pathways.tyrant;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities.*;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TyrantItems extends Items {

    public TyrantItems(Pathway pathway) {
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
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "raging-blows")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "water-spells")
        );
        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "wind-manipulation")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "lightning")
        );
        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "siren-song")
        );
        abilityInfo.put(5, s5);

        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "roar"),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "tornado")
        );
        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "tsunami"),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "lightning-storm")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "extreme-coldness")
        );
        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "lightning-tornado"),
                LocalizationUtil.getLocalizedString("tyrant", "abilities", "lightning-ball")
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

    private void createItems() {
        addAbility(new RagingBlows(1, pathway, 9, this));

        addAbility(new WaterSpells(2, pathway, 8, this));

        addAbility(new WindManipulation(3, pathway, 7, this));

        addAbility(new Lightning(4, pathway, 6, this));

        addAbility(new SirenSong(5, pathway, 5, this));

        addAbility(new Roar(6, pathway, 4, this));
        addAbility(new Tornado(7, pathway, 4, this));

        addAbility(new Tsunami(8, pathway, 3, this));
        addAbility(new LightningStorm(9, pathway, 3, this));

        addAbility(new ExtremeColdness(10, pathway, 2, this));

        addAbility(new LightningTornado(11, pathway, 1, this));
        addAbility(new LightningBall(12, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
