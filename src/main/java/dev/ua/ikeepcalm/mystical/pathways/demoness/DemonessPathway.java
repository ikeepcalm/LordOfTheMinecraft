package dev.ua.ikeepcalm.mystical.pathways.demoness;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.utils.LocalizationUtil;

import java.util.HashMap;
import java.util.UUID;

public class DemonessPathway extends Pathway {

    public DemonessPathway(UUID uuid, int optionalSequence, int pathwayInt) {
        super(uuid, optionalSequence, pathwayInt);
    }

    @Override
    public void init() {
        sequence = new DemonessSequence(this, optionalSequence);
        name = ColorAPI.colorize(LocalizationUtil.getLocalizedString("demoness", "color") + LocalizationUtil.getLocalizedString("demoness", "name"));
        stringColor = ColorAPI.colorize(LocalizationUtil.getLocalizedString("demoness", "color"));
        nameNormalized = "demoness";
    }

    @Override
    public void initItems() {
        items = new DemonessItems(getPathway());
    }

    public static HashMap<Integer, String> getNames() {
        HashMap<Integer, String> names;
        names = new HashMap<>();
        names.put(9, LocalizationUtil.getLocalizedString("demoness", "sequences","9"));
        names.put(8, LocalizationUtil.getLocalizedString("demoness", "sequences","8"));
        names.put(7, LocalizationUtil.getLocalizedString("demoness", "sequences","7"));
        names.put(6, LocalizationUtil.getLocalizedString("demoness", "sequences","6"));
        names.put(5, LocalizationUtil.getLocalizedString("demoness", "sequences","5"));
        names.put(4, LocalizationUtil.getLocalizedString("demoness", "sequences","4"));
        names.put(3, LocalizationUtil.getLocalizedString("demoness", "sequences","3"));
        names.put(2, LocalizationUtil.getLocalizedString("demoness", "sequences","2"));
        names.put(1, LocalizationUtil.getLocalizedString("demoness", "sequences","1"));
        return names;
    }

}
