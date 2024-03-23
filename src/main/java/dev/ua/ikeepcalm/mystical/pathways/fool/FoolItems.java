package dev.ua.ikeepcalm.mystical.pathways.fool;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.*;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.marionetteAbilities.MarionetteControlling;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.marionetteAbilities.SpiritBodyThreads;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FoolItems extends Items {

    public FoolItems(Pathway pathway) {
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
                LocalizationUtil.getLocalizedString("fool", "abilities", "divination")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("fool", "abilities", "flame-controlling"),
                LocalizationUtil.getLocalizedString("fool", "abilities", "air-bullet")
        );

        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("fool", "abilities", "air-pipe"),
                LocalizationUtil.getLocalizedString("fool", "abilities", "flaming-jump")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("fool", "abilities", "paper-figurine-substitute")
        );
        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("fool", "abilities", "spirit-body-threads"),
                LocalizationUtil.getLocalizedString("fool", "abilities", "marionette-controlling")
        );
        abilityInfo.put(5, s5);

//        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
//                LocalizationUtil.getLocalizedString("fool", "miscellaneous", "mob-enhancement"),
//                LocalizationUtil.getLocalizedString("fool", "miscellaneous", "mc-upgrade")
//        );
//        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("fool", "abilities", "fog-of-history"),
                LocalizationUtil.getLocalizedString("fool", "abilities", "hiding-in-the-foh")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("fool", "abilities", "miracles")
        );
        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("fool", "abilities", "grafting"),
                LocalizationUtil.getLocalizedString("fool", "abilities", "realm-of-mysteries")
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
        SpiritBodyThreads spiritBodyThreads = new SpiritBodyThreads(7, pathway, 5, this, false);

        addAbility(new Divine(1, pathway, 9, this));

        addAbility(new FlameControlling(2, pathway, 8, this, false));
        addAbility(new AirBullet(3, pathway, 8, this, false));

        addAbility(new AirPipe(4, pathway, 7, this));
        addAbility(new FlameJump(5, pathway, 7, this));

        addAbility(new PaperSubstitute(6, pathway, 6, this));

        addAbility(spiritBodyThreads);
        addAbility(new MarionetteControlling(8, pathway, 5, this, spiritBodyThreads));

        addAbility(new FogOfHistory(9, pathway, 3, this));
        addAbility(new Hiding(10, pathway, 3, this));

        addAbility(new Miracles(11, pathway, 2, this));

        addAbility(new Grafting(12, pathway, 1, this, false));
        addAbility(new RealmOfMysteries(13, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }

}
