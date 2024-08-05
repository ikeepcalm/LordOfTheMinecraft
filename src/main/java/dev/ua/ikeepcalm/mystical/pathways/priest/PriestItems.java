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
                LocalizationUtil.getLocalizedString("priest", "abilities", "flame-armor")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("priest", "abilities", "flame-wall"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "conspiracy")
        );

        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("priest", "abilities", "cull")
        );
        abilityInfo.put(5, s5);

        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
                LocalizationUtil.getLocalizedString("priest", "abilities", "transmutation"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "chain-of-control")
        );
        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("priest", "abilities", "war-cry"),
                LocalizationUtil.getLocalizedString("priest", "abilities", "fog-of-war")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("priest", "abilities", "weather-manipulation")
        );

        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("priest", "abilities", "conquering")
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
        addAbility(new Trap(1, pathway, 9, this));
        addAbility(new Tracking(2, pathway, 9, this));
        addAbility(new Provoke(3, pathway, 8, this));
        addAbility(new FireRaven(4, pathway, 7, this));
        addAbility(new BlazingSpear(5, pathway, 7, this));
        addAbility(new FlameArmor(6, pathway, 7, this));
        addAbility(new FlameWall(7, pathway, 6, this));
        addAbility(new Conspiracy(8, pathway, 6, this));
        addAbility(new Cull(9, pathway, 5, this));
        addAbility(new Transmutation(10, pathway, 4, this));
        addAbility(new ChainControl(11, pathway, 4, this));
        addAbility(new WarCry(12, pathway, 3, this));
        addAbility(new FogOfWar(13, pathway, 3, this));
        addAbility(new WeatherManipulation(14, pathway, 2, this));
        addAbility(new Conquering(15, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
