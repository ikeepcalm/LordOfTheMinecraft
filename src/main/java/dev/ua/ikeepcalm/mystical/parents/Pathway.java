package dev.ua.ikeepcalm.mystical.parents;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessPathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorPathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolPathway;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestPathway;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunPathway;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantPathway;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public abstract class Pathway {

    @Getter
    protected final UUID uuid;
    @Setter
    @Getter
    protected Sequence sequence;
    @Setter
    @Getter
    protected String name;
    @Setter
    @Getter
    protected Beyonder beyonder;
    protected final int optionalSequence;
    @Getter
    protected String stringColor;
    @Getter
    protected String nameNormalized;
    @Getter
    protected final int pathwayInt;

    @Setter
    @Getter
    public Items items;

    public Pathway(UUID uuid, int optionalSequence, int pathwayInt) {
        this.uuid = uuid;
        this.optionalSequence = optionalSequence;
        this.pathwayInt = pathwayInt;
    }


    public void init() {

    }

    public Pathway getPathway() {
        return this;
    }


    //Initializes a new Pathway
    //Called from BeyonderCmd, Plugin and PotionListener
    public static Pathway initializeNew(String pathway, UUID uuid, int sequence) {
        Pathway pathwayObject;
        if (LordOfTheMinecraft.beyonders.containsKey(uuid))
            return null;
        switch (pathway) {
            case "sun" -> pathwayObject = new SunPathway(uuid, sequence, 0);
            case "fool" -> pathwayObject = new FoolPathway(uuid, sequence, 1);
            case "door" -> pathwayObject = new DoorPathway(uuid, sequence, 2);
            case "demoness" -> pathwayObject = new DemonessPathway(uuid, sequence, 3);
            case "tyrant" -> pathwayObject = new TyrantPathway(uuid, sequence, 4);
            case "priest" -> pathwayObject = new PriestPathway(uuid, sequence, 5);
            default -> {
                return null;
            }
        }

        Beyonder beyonder = new Beyonder(uuid, pathwayObject, 0, 0);
        LordOfTheMinecraft.beyonders.put(uuid, beyonder);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(beyonder, LordOfTheMinecraft.instance);
        return pathwayObject;

    }

    public static void initializeNew(String pathway, UUID uuid, int sequence, int acting, int spirituality) {
        Pathway pathwayObject;
        if (LordOfTheMinecraft.beyonders.containsKey(uuid))
            return;
        switch (pathway) {
            case "sun" -> pathwayObject = new SunPathway(uuid, sequence, 0);
            case "fool" -> pathwayObject = new FoolPathway(uuid, sequence, 1);
            case "door" -> pathwayObject = new DoorPathway(uuid, sequence, 2);
            case "demoness" -> pathwayObject = new DemonessPathway(uuid, sequence, 3);
            case "tyrant" -> pathwayObject = new TyrantPathway(uuid, sequence, 4);
            default -> {
                return;
            }
        }

        Beyonder beyonder = new Beyonder(uuid, pathwayObject, acting, spirituality);
        LordOfTheMinecraft.beyonders.put(uuid, beyonder);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(beyonder, LordOfTheMinecraft.instance);
    }

    public abstract void initItems();


    public static HashMap<Integer, String> getNamesForPathway(String pathway) {
        switch (pathway.toLowerCase()) {
            case "sun" -> {
                return SunPathway.getNames();
            }
            case "fool" -> {
                return FoolPathway.getNames();
            }
            case "door" -> {
                return DoorPathway.getNames();
            }
            case "demoness" -> {
                return DemonessPathway.getNames();
            }
            case "tyrant" -> {
                return TyrantPathway.getNames();
            }
            case "priest" -> {
                return PriestPathway.getNames();
            }
            default -> {
                return null;
            }
        }
    }

}

