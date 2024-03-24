package dev.ua.ikeepcalm.mystical.pathways.door;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.utils.LocalizationUtil;

import java.util.HashMap;
import java.util.UUID;

public class DoorPathway extends Pathway {

    public DoorPathway(UUID uuid, int optionalSequence, int pathwayInt) {
        super(uuid, optionalSequence, pathwayInt);
    }

    @Override
    public void init() {
        sequence = new DoorSequence(this, optionalSequence);
        name = ColorAPI.colorize(LocalizationUtil.getLocalizedString("door", "color") + LocalizationUtil.getLocalizedString("door", "name"));
        stringColor = ColorAPI.colorize(LocalizationUtil.getLocalizedString("door", "color"));
        nameNormalized = "door";
    }

    @Override
    public void initItems() {
        items = new DoorItems(getPathway());
    }

    public static HashMap<Integer, String> getNames() {
        HashMap<Integer, String> names;
        names = new HashMap<>();
        names.put(9, LocalizationUtil.getLocalizedString("door", "sequences","9"));
        names.put(8, LocalizationUtil.getLocalizedString("door", "sequences","8"));
        names.put(7, LocalizationUtil.getLocalizedString("door", "sequences","7"));
        names.put(6, LocalizationUtil.getLocalizedString("door", "sequences","6"));
        names.put(5, LocalizationUtil.getLocalizedString("door", "sequences","5"));
        names.put(4, LocalizationUtil.getLocalizedString("door", "sequences","4"));
        names.put(3, LocalizationUtil.getLocalizedString("door", "sequences","3"));
        names.put(2, LocalizationUtil.getLocalizedString("door", "sequences","2"));
        names.put(1, LocalizationUtil.getLocalizedString("door", "sequences","1"));
        return names;
    }

}
