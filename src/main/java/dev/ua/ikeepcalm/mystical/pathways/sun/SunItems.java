package dev.ua.ikeepcalm.mystical.pathways.sun;

import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.abilities.*;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SunItems extends Items {

    public SunItems(Pathway pathway) {
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
                LocalizationUtil.getLocalizedString("sun", "abilities", "holy-song"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "holy-light")
        );
        abilityInfo.put(9, s9);

        String[] s8 = formatAbilityInfo(pathway.getStringColor(), "8: " + names.get(8),
                LocalizationUtil.getLocalizedString("sun", "abilities", "illuminate"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "fire-of-light")
        );
        abilityInfo.put(8, s8);

        String[] s7 = formatAbilityInfo(pathway.getStringColor(), "7: " + names.get(7),
                LocalizationUtil.getLocalizedString("sun", "abilities", "holy-light-summoning"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "holy-oath"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "cleave-of-purification")
        );
        abilityInfo.put(7, s7);

        String[] s6 = formatAbilityInfo(pathway.getStringColor(), "6: " + names.get(6),
                LocalizationUtil.getLocalizedString("sun", "abilities", "light-of-holiness"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "light-of-purification")
        );
        abilityInfo.put(6, s6);

        String[] s5 = formatAbilityInfo(pathway.getStringColor(), "5: " + names.get(5),
                LocalizationUtil.getLocalizedString("sun", "abilities", "unshadowed-spear"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "flaring-sun")
        );
        abilityInfo.put(5, s5);

        String[] s4 = formatAbilityInfo(pathway.getStringColor(), "4: " + names.get(4),
                LocalizationUtil.getLocalizedString("sun", "abilities", "unshadowed-domain"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "armor-of-light")
        );
        abilityInfo.put(4, s4);

        String[] s3 = formatAbilityInfo(pathway.getStringColor(), "3: " + names.get(3),
                LocalizationUtil.getLocalizedString("sun", "abilities", "spear-of-light"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "beam-of-light")
        );
        abilityInfo.put(3, s3);

        String[] s2 = formatAbilityInfo(pathway.getStringColor(), "2: " + names.get(2),
                LocalizationUtil.getLocalizedString("sun", "abilities", "ocean-of-light"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "wings-of-light")
        );
        abilityInfo.put(2, s2);

        String[] s1 = formatAbilityInfo(pathway.getStringColor(), "1: " + names.get(1),
                LocalizationUtil.getLocalizedString("sun", "abilities", "day-and-night"),
                LocalizationUtil.getLocalizedString("sun", "abilities", "solar-flare")
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
        addAbility(new HolySong(1, pathway, 9, this));
        addAbility(new HolyLight(2, pathway, 9, this));

        addAbility(new Illuminate(3, pathway, 8, this));
        addAbility(new FireOfLight(4, pathway, 8, this));

        addAbility(new HolyLightSummoning(5, pathway, 7, this));
        addAbility(new HolyOath(6, pathway, 7, this));
        addAbility(new CleaveOfPurification(7, pathway, 7, this));

        addAbility(new LightOfHoliness(8, pathway, 6, this));
        addAbility(new LightOfPurification(9, pathway, 6, this));

        addAbility(new UnshadowedSpear(10, pathway, 5, this));
        addAbility(new FlaringSun(11, pathway, 5, this));

        addAbility(new UnshadowedDomain(12, pathway, 4, this));
        addAbility(new ArmorOfLight(13, pathway, 4, this));

        addAbility(new SpearOfLight(15, pathway, 3, this));
        addAbility(new BeamOfLight(14, pathway, 3, this));

        addAbility(new OceanOfLight(16, pathway, 2, this));
        addAbility(new WingsOfLight(17, pathway, 2, this));

        addAbility(new DayAndNight(18, pathway, 1, this));
        addAbility(new SolarFlare(19, pathway, 1, this));
    }

    public void addAbility(Ability ability) {
        pathway.getSequence().getAbilities().add(ability);
        items.add(ability.getItem());
    }
}
