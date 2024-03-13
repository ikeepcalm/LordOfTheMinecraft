package dev.ua.ikeepcalm.mystical.pathways.tyrant;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.utils.LocalizationUtil;

import java.util.HashMap;
import java.util.UUID;

public class TyrantPathway extends Pathway {

    public TyrantPathway(UUID uuid, int optionalSequence, int pathwayInt) {
        super(uuid, optionalSequence, pathwayInt);
    }

    @Override
    public void init() {
        sequence = new TyrantSequence(this, optionalSequence);
        name = ColorAPI.colorize(LocalizationUtil.getLocalizedString("tyrant", "color") + LocalizationUtil.getLocalizedString("tyrant", "name"));
        stringColor = ColorAPI.colorize(LocalizationUtil.getLocalizedString("tyrant", "color"));
        nameNormalized = "tyrant";
    }

    @Override
    public void initItems() {
        items = new TyrantItems(getPathway());
    }

    public static HashMap<Integer, String> getNames() {
        HashMap<Integer, String> names;
        names = new HashMap<>();
        names.put(9, LocalizationUtil.getLocalizedString("tyrant", "sequences","9"));
        names.put(8, LocalizationUtil.getLocalizedString("tyrant", "sequences","8"));
        names.put(7, LocalizationUtil.getLocalizedString("tyrant", "sequences","7"));
        names.put(6, LocalizationUtil.getLocalizedString("tyrant", "sequences","6"));
        names.put(5, LocalizationUtil.getLocalizedString("tyrant", "sequences","5"));
        names.put(4, LocalizationUtil.getLocalizedString("tyrant", "sequences","4"));
        names.put(3, LocalizationUtil.getLocalizedString("tyrant", "sequences","3"));
        names.put(2, LocalizationUtil.getLocalizedString("tyrant", "sequences","2"));
        names.put(1, LocalizationUtil.getLocalizedString("tyrant", "sequences","1"));
        return names;
    }

}
