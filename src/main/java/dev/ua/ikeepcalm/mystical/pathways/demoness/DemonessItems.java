package dev.ua.ikeepcalm.mystical.pathways.demoness;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.abilities.*;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DemonessItems extends Items {

    public DemonessItems(Pathway pathway) {
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
                LocalizationUtil.getLocalizedString("demoness", "abilities", "instigate")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "invisibility"),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "black-flames")
        );
        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "frost-magic"),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "cold-wind")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "frost-spear"),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "thread-manipulation")
        );
        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "epidemic"),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "mirror-world-traversal")
        );
        abilityInfo.put(5, s5);

        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "pestilence")
        );
        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "petrification")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "calamity-manipulation")
        );
        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "meteor-shower"),
                LocalizationUtil.getLocalizedString("demoness", "abilities", "ice-age")
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
        addAbility(new Instigate(1, pathway, 9, this));
        addAbility(new Invisibility(2, pathway, 8, this));
        addAbility(new DarkFlames(3, pathway, 8, this));
        addAbility(new FrostMagic(4, pathway, 7, this));
        addAbility(new ColdWind(5, pathway, 7, this));
        addAbility(new FrostSpear(6, pathway, 6, this));
        addAbility(new ThreadManipulation(7, pathway, 6, this));
        addAbility(new Epidemic(8, pathway, 5, this));
        addAbility(new MirrorWorldTraversal(9, pathway, 5, this));
        addAbility(new Pestilence(10, pathway, 4, this));
        addAbility(new Petrification(11, pathway, 3, this));
        addAbility(new CalamityManipulation(12, pathway, 2, this));
        addAbility(new MeteorShower(13, pathway, 1, this));
        addAbility(new IceAge(14, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
