package dev.ua.ikeepcalm.mystical.pathways.fool;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.utils.LocalizationUtil;

import java.util.HashMap;
import java.util.UUID;

public class FoolPathway extends Pathway {

    public FoolPathway(UUID uuid, int optionalSequence, int pathwayInt) {
        super(uuid, optionalSequence, pathwayInt);
    }

    @Override
    public void init() {
        sequence = new FoolSequence(this, optionalSequence);
        name = ColorAPI.colorize(LocalizationUtil.getLocalizedString("fool", "color") + LocalizationUtil.getLocalizedString("fool", "name"));
        stringColor = ColorAPI.colorize(LocalizationUtil.getLocalizedString("fool", "color"));
        nameNormalized = "fool";
    }

    @Override
    public void initItems() {
        items = new FoolItems(this);
    }

    public static HashMap<Integer, String> getNames() {
        HashMap<Integer, String> names;
        names = new HashMap<>();
        names.put(9, LocalizationUtil.getLocalizedString("fool", "sequences","9"));
        names.put(8, LocalizationUtil.getLocalizedString("fool", "sequences","8"));
        names.put(7, LocalizationUtil.getLocalizedString("fool", "sequences","7"));
        names.put(6, LocalizationUtil.getLocalizedString("fool", "sequences","6"));
        names.put(5, LocalizationUtil.getLocalizedString("fool", "sequences","5"));
        names.put(4, LocalizationUtil.getLocalizedString("fool", "sequences","4"));
        names.put(3, LocalizationUtil.getLocalizedString("fool", "sequences","3"));
        names.put(2, LocalizationUtil.getLocalizedString("fool", "sequences","2"));
        names.put(1, LocalizationUtil.getLocalizedString("fool", "sequences","1"));
        return names;
    }
}
