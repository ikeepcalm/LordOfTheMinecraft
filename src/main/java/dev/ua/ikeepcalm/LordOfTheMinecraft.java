package dev.ua.ikeepcalm;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.cmds.*;
import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.handlers.ArtifactHandler;
import dev.ua.ikeepcalm.handlers.BlockHandler;
import dev.ua.ikeepcalm.handlers.MobsHandler;
import dev.ua.ikeepcalm.handlers.SpiritHandler;
import dev.ua.ikeepcalm.listeners.*;
import dev.ua.ikeepcalm.listeners.beyonders.RogueBeyonder;
import dev.ua.ikeepcalm.listeners.beyonders.RogueBeyonders;
import dev.ua.ikeepcalm.mystical.artifacts.SealedArtifacts;
import dev.ua.ikeepcalm.mystical.parents.*;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessPotions;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorPotions;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolPotions;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.FogOfHistory;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunPotions;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantPotions;
import dev.ua.ikeepcalm.utils.AbilityInitHandUtil;
import dev.ua.ikeepcalm.utils.BossBarUtil;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.*;

public final class LordOfTheMinecraft extends JavaPlugin {

    public static LordOfTheMinecraft instance;
    public static String prefix;
    @Getter
    private Characteristic characteristic;
    @Getter
    private Recipe recipe;
    private MobsHandler mobsHandler;
    @Getter
    private RogueBeyonders rogueBeyonders;
    @Getter
    private SpiritHandler spiritHandler;
    @Getter
    private SealedArtifacts sealedArtifacts;
    public static ArrayList<RogueBeyonder> currentRogueBeyonders;
    public static HashMap<UUID, Beyonder> beyonders;
    public static HashMap<UUID, ServerPlayer> fakePlayers = new HashMap<>();
    public static final HashMap<UUID, FogOfHistory> fogOfHistories = new HashMap<>();
    public static BossBarUtil bossBarUtil;
    @Getter
    public FileConfiguration langConfig;
    @Getter
    private ArrayList<ArrayList<Entity>> concealedEntities;
    private File configSaveFile;
    private FileConfiguration configSave;
    private File configSaveFileFoh;
    private FileConfiguration configSaveFoh;
    @Getter
    private ArrayList<Potion> potions;
    @Getter
    private Divination divination;
    public static UUID randomUUID;
    @Getter
    private ArrayList<String> names;

    @Override
    public void onLoad() {
        createSaveLangConfig();
        prefix = "§8[§5Lord of the Minecraft§8] ";
        randomUUID = UUID.fromString("1af36f3a-d8a3-11ed-afa1-0242ac120002");
        instance = this;
        beyonders = new HashMap<>();
        fakePlayers = new HashMap<>();
        currentRogueBeyonders = new ArrayList<>();
        recipe = new Recipe();
        concealedEntities = new ArrayList<>();
        names = new ArrayList<>();
        bossBarUtil = new BossBarUtil();
    }

    @Override
    public void onEnable() {

        try {
            characteristic = new Characteristic();
        } catch (MalformedURLException ignored) {}

        initHandlerClasses();

        try {
            createSaveConfig();
            loadNames();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(prefix + "§aEnabled Plugin");

        register();
        initPotions();
        createSaveConfigFoH();

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity.hasMetadata("Beyonder")) {
                            CitizensAPI.getNPCRegistry().getByUniqueId(entity.getUniqueId()).destroy();
                        }
                    }
                }
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 10);
    }

    private void initHandlerClasses() {
        spiritHandler = new SpiritHandler();
        sealedArtifacts = new SealedArtifacts();
        new AbilityInitHandUtil();
        rogueBeyonders = new RogueBeyonders();
    }

    //register all the Listeners and CommandExecutors
    public void register() {
        divination = new Divination();
        mobsHandler = new MobsHandler();

        registerEvents(
                new InteractListener(),
                new PotionHandler(),
                new PotionListener(),
                new DeathListener(),
                divination,
                mobsHandler,
                new BlockHandler(),
                new GenerationListener(),
                new ArtifactHandler()
        );

        Objects.requireNonNull(this.getCommand("beyonder")).setExecutor(new BeyonderCmd());
        Objects.requireNonNull(this.getCommand("test")).setExecutor(new TestCmd());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCmd());
        Objects.requireNonNull(this.getCommand("ability-info")).setExecutor(new AbilityInfoCmd());
    }

    private void registerEvents(Listener... listeners) {
        PluginManager pl = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pl.registerEvents(listener, this);
        }
    }

    //initialize the Potion Classes
    public void initPotions() {
        potions = new ArrayList<>();
        potions.add(new SunPotions());
        potions.add(new FoolPotions());
        potions.add(new DoorPotions());
        potions.add(new DemonessPotions());
        potions.add(new TyrantPotions());
    }

    private void loadNames() throws InterruptedException {
        File namesFile = new File(getDataFolder(), "names.yml");
        FileConfiguration configNames = new YamlConfiguration();


        if (!namesFile.exists()) {
            saveResource("names.yml", true);
        }

        Thread.sleep(500);

        try {
            configNames.load(namesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        names.addAll(configNames.getStringList("names"));
    }

    @Override
    //call the save function to save the beyonders.yml file and the fools.yml file
    public void onDisable() {
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveResource("fools.yml", true);

        for (FogOfHistory foh : fogOfHistories.values()) {
            try {
                saveFoH(foh);
                configSaveFoh.save(configSaveFileFoh);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //loop through all the items and place them in the fools.yml file
    //fools:
    //    uuid:
    //        int:
    //            itemstack
    //        int:
    //            itemstack
    private void saveFoH(FogOfHistory foh) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§aSaving Fog of History Inventories");

        for (int i = 0; i < foh.getItems().size(); i++) {
            configSaveFoh.set("fools." + foh.getPathway().getBeyonder().getUuid() + ("." + i), foh.getItems().get(i));
        }
    }

    //create the config file if it doesn't exist and then load the config
    public void createSaveConfig() throws InterruptedException {
        configSaveFile = new File(getDataFolder(), "beyonders.yml");
        if (!configSaveFile.exists()) {
            if (configSaveFile.getParentFile().mkdirs())
                saveResource("beyonders.yml", false);

            else
                Bukkit.getConsoleSender().sendMessage("§cSomething went wrong while saving the beyonders.yml file");
        }

        Thread.sleep(1000);

        configSave = new YamlConfiguration();
        try {
            configSave.load(configSaveFile);
        } catch (InvalidConfigurationException | IOException exc) {
            Bukkit.getConsoleSender().sendMessage(exc.getLocalizedMessage());
        }
        load();
    }

    //create the config foh file if it doesn't exist and then load the config foh
    private void createSaveConfigFoH() {
        configSaveFileFoh = new File(getDataFolder(), "fools.yml");
        if (!configSaveFileFoh.exists()) {
            saveResource("fools.yml", true);
        }

        configSaveFoh = new YamlConfiguration();

        try {
            configSaveFoh.load(configSaveFileFoh);
        } catch (InvalidConfigurationException | IOException exc) {
            Bukkit.getConsoleSender().sendMessage(exc.getLocalizedMessage());
        }

        loadFoh();
    }

    private void createSaveLangConfig() {
        File langConfigFile = new File(getDataFolder(), "lang.yml");
        if (!langConfigFile.exists()) {
            saveResource("lang.yml", true);
        }

        langConfig = new YamlConfiguration();

        try {
            langConfig.load(langConfigFile);
        } catch (InvalidConfigurationException | IOException exc) {
            Bukkit.getConsoleSender().sendMessage(exc.getLocalizedMessage());
        }
    }

    //remove beyonder from list and yml file
    public void removeBeyonder(UUID uuid) {
        beyonders.remove(uuid);
        configSave.set("beyonders." + uuid, null);
        try {
            configSave.save(configSaveFile);
        } catch (IOException exc) {
            Bukkit.getConsoleSender().sendMessage(exc.getLocalizedMessage());
        }
    }

    //save all the beyonders to the config
    //beyonders:
    //    uuid:
    //        pathway: "pathway-name"
    //        sequence: "sequence-number"
    public void save() throws IOException {
        Bukkit.getConsoleSender().sendMessage(prefix + "§aSaving Beyonders");

        for (Map.Entry<UUID, Beyonder> entry : beyonders.entrySet()) {
            configSave.set("beyonders." + entry.getKey() + ".pathway", entry.getValue().getPathway().getNameNormalized());
            configSave.set("beyonders." + entry.getKey() + ".sequence", entry.getValue().getPathway().getSequence().getCurrentSequence());
        }
        configSave.save(configSaveFile);
    }


    public void loadFoh() {
        if (configSaveFoh.getConfigurationSection("fools") == null)
            return;

        for (String s : Objects.requireNonNull(configSaveFoh.getConfigurationSection("fools")).getKeys(false)) {
            if (fogOfHistories.get(UUID.fromString(s)) == null)
                return;

            if (configSaveFoh.get("fools." + s) == null)
                return;

            for (String t : Objects.requireNonNull(configSaveFoh.getConfigurationSection("fools." + s)).getKeys(false)) {
                int i = parseInt(t);
                if (i == -1)
                    return;

                ItemStack item = configSaveFoh.getItemStack("fools." + s + "." + i);
                if (item == null)
                    continue;
                for (FogOfHistory fogOfHistory : fogOfHistories.values()) {
                    if (fogOfHistory.getPathway().getBeyonder().getUuid().equals(UUID.fromString(s))) {
                        fogOfHistory.addItem(item);
                    }
                }
            }
        }
    }

    //return -1 if string is not an integer
    public Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    //load all the beyonders from beyonders.yml and initialize their mystical
    public void load() {
        if (configSave.getConfigurationSection("beyonders") == null) {
            configSave.set("beyonders.uuid.pathway", "pathway-name");
            configSave.set("beyonders.uuid.sequence", "sequence");
        }
        for (String s : Objects.requireNonNull(configSave.getConfigurationSection("beyonders")).getKeys(false)) {
            if (s.equals("uuid"))
                continue;
            try {
                if (!configSave.contains("beyonders." + s + ".sequence") || !(configSave.get("beyonders." + s + ".sequence") instanceof Integer sequence))
                    return;

                int primitiveSequence = sequence;
                Pathway.initializeNew((String) Objects.requireNonNull(configSave.get("beyonders." + s + ".pathway")), UUID.fromString(s), primitiveSequence);
            } catch (Exception exception) {
                Bukkit.getConsoleSender().sendMessage("Failed to initialize " + s);

                //Error message
                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Bukkit.getConsoleSender().sendMessage("§c" + exceptionAsString);
            }
        }
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ColorAPI.colorize(prefix + "{#ff005d}" + message));
    }

    public MobsHandler getBeyonderMobsHandler() {
        return mobsHandler;
    }

    public void addToConcealedEntities(ArrayList<Entity> list) {
        concealedEntities.add(list);
    }

    public void removeFromConcealedEntities(ArrayList<Entity> list) {
        concealedEntities.remove(list);
    }

    public void removeRogueBeyonder(RogueBeyonder rogueBeyonder) {
        rogueBeyonder.remove();
    }

    public void addRogueBeyonder(RogueBeyonder rogueBeyonder) {
        currentRogueBeyonders.add(rogueBeyonder);
    }

    public ArrayList<RogueBeyonder> getCurrentRogueBeyonders() {
        return currentRogueBeyonders;
    }

}
