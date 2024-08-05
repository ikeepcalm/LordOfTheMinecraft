package dev.ua.ikeepcalm.mystical.pathways.priest;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.utils.LocalizationUtil;

import java.util.HashMap;
import java.util.UUID;

public class PriestPathway extends Pathway {

    public PriestPathway(UUID uuid, int optionalSequence, int pathwayInt) {
        super(uuid, optionalSequence, pathwayInt);
    }

    @Override
    public void init() {
        sequence = new PriestSequence(this, optionalSequence);
        name = ColorAPI.colorize(LocalizationUtil.getLocalizedString("priest", "color") + LocalizationUtil.getLocalizedString("priest", "name"));
        stringColor = ColorAPI.colorize(LocalizationUtil.getLocalizedString("priest", "color"));
        nameNormalized = "priest";
    }

    @Override
    public void initItems() {
        items = new PriestItems(getPathway());
    }

    public static HashMap<Integer, String> getNames() {
        HashMap<Integer, String> names;
        names = new HashMap<>();
        names.put(9, LocalizationUtil.getLocalizedString("priest", "sequences", "9"));
        names.put(8, LocalizationUtil.getLocalizedString("priest", "sequences", "8"));
        names.put(7, LocalizationUtil.getLocalizedString("priest", "sequences", "7"));
        names.put(6, LocalizationUtil.getLocalizedString("priest", "sequences", "6"));
        names.put(5, LocalizationUtil.getLocalizedString("priest", "sequences", "5"));
        names.put(4, LocalizationUtil.getLocalizedString("priest", "sequences", "4"));
        names.put(3, LocalizationUtil.getLocalizedString("priest", "sequences", "3"));
        names.put(2, LocalizationUtil.getLocalizedString("priest", "sequences", "2"));
        names.put(1, LocalizationUtil.getLocalizedString("priest", "sequences", "1"));
        return names;
    }

}
