package dev.ua.ikeepcalm;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.cmds.BoonCmd;
import dev.ua.ikeepcalm.cmds.MI9Cmd;
import dev.ua.ikeepcalm.cmds.SpawnCmd;
import dev.ua.ikeepcalm.cmds.TestCmd;
import dev.ua.ikeepcalm.handlers.BlockHandler;
import dev.ua.ikeepcalm.handlers.MobsHandler;
import dev.ua.ikeepcalm.handlers.SpiritHandler;
import dev.ua.ikeepcalm.listeners.*;
import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.*;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessPotions;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorPotions;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolPotions;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.FogOfHistory;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunPotions;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantPotions;
import dev.ua.ikeepcalm.utils.BossBarUtil;
import lombok.Getter;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public final class LordOfTheMinecraft extends JavaPlugin {

    public static LordOfTheMinecraft instance;
    public static CoreProtectAPI coreProtect;
    public static String prefix;
    @Getter
    private Characteristic characteristic;
    @Getter
    private Recipe recipe;
    private MobsHandler mobsHandler;
    @Getter
    private SpiritHandler spiritHandler;
    public static HashMap<UUID, Beyonder> beyonders;
    public static HashMap<UUID, Player> fakePlayers = new HashMap<>();
    public static final HashMap<UUID, FogOfHistory> fogOfHistories = new HashMap<>();
    public static BossBarUtil bossBarUtil;
    @Getter
    public FileConfiguration langConfig;
    @Getter
    public FileConfiguration excConfig;
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

    @Override
    public void onLoad() {
        createSaveLangConfig();
        createSaveExcConfig();
        prefix = "§8[§5Lord of the Minecraft§8] ";
        randomUUID = UUID.fromString("1af36f3a-d8a3-11ed-afa1-0242ac120002");
        instance = this;
        beyonders = new HashMap<>();
        fakePlayers = new HashMap<>();
        recipe = new Recipe();
        concealedEntities = new ArrayList<>();
        bossBarUtil = new BossBarUtil();
        potions = new ArrayList<>();
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        loadCoreProtect();
        enablePlugin();

        Bukkit.getConsoleSender().sendMessage(prefix + "§aEnabled. The world full of mysteries awaits you!");

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }
    }

    private void enablePlugin() {
        characteristic = new Characteristic();
        spiritHandler = new SpiritHandler();
        try {
            createBeyondersConfig();
            createFoHConfig();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        divination = new Divination();
        mobsHandler = new MobsHandler();

        registerEvents(
                new InteractListener(),
                new PotionHandler(),
                new PotionListener(),
                new DeathListener(),
                new ExplosionListener(),
                new WantedListener(),
                divination,
                new BlockHandler(),
                new GenerationListener(),
                new MI9ItemsListener()
        );

        if (getConfig().getBoolean("enable-mobs")) {
            registerEvents(mobsHandler);
        }

        Objects.requireNonNull(this.getCommand("boon")).setExecutor(new BoonCmd());
        Objects.requireNonNull(this.getCommand("test")).setExecutor(new TestCmd());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCmd());
        Objects.requireNonNull(this.getCommand("mi9")).setExecutor(new MI9Cmd());

        potions.add(new SunPotions());
        potions.add(new FoolPotions());
        potions.add(new DoorPotions());
        potions.add(new DemonessPotions());
        potions.add(new TyrantPotions());
    }


    private void registerEvents(Listener... listeners) {
        PluginManager pl = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pl.registerEvents(listener, this);
        }
    }

    @Override
    //call the save function to save the beyonders.yml file and the fools.yml file
    public void onDisable() {
        try {
            save();
        } catch (IOException e) {
            log("Failed to save beyonders.yml");
        }

        saveResource("fools.yml", true);

        for (FogOfHistory foh : fogOfHistories.values()) {
            try {
                saveFoH(foh);
                configSaveFoh.save(configSaveFileFoh);
            } catch (IOException e) {
                log("Failed to save fog of history");
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
    public void createBeyondersConfig() throws InterruptedException {
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
        loadBeyonders();
    }

    //create the config foh file if it doesn't exist and then load the config foh
    private void createFoHConfig() {
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
        loadFohConfig();
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

    private void createSaveExcConfig() {
        File langConfigFile = new File(getDataFolder(), "exc.yml");
        if (!langConfigFile.exists()) {
            saveResource("exc.yml", true);
        }

        excConfig = new YamlConfiguration();

        try {
            excConfig.load(langConfigFile);
        } catch (InvalidConfigurationException | IOException exc) {
            Bukkit.getConsoleSender().sendMessage(exc.getLocalizedMessage());
        }
    }

    public void saveExcConfig() {
        try {
            excConfig.save(new File(getDataFolder(), "exc.yml"));
        } catch (IOException exc) {
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
            configSave.set("beyonders." + entry.getKey() + ".acting", (int) entry.getValue().getActingProgress());
            configSave.set("beyonders." + entry.getKey() + ".spirituality", (int) entry.getValue().getSpirituality());
        }
        configSave.save(configSaveFile);
    }


    public void loadFohConfig() {
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
    public void loadBeyonders() {
        if (configSave.getConfigurationSection("beyonders") == null) {
            configSave.set("beyonders.uuid.pathway", "pathway-name");
            configSave.set("beyonders.uuid.sequence", "sequence");
            configSave.set("beyonders.uuid.acting", "acting-value");
            configSave.set("beyonders.uuid.spirituality", "spirituality-value");
        }
        for (String s : Objects.requireNonNull(configSave.getConfigurationSection("beyonders")).getKeys(false)) {
            if (s.equals("uuid"))
                continue;
            try {
                String pathway = configSave.getString("beyonders." + s + ".pathway");
                int sequence = configSave.getInt("beyonders." + s + ".sequence");
                int acting = configSave.getInt("beyonders." + s + ".acting");
                int spirituality = configSave.getInt("beyonders." + s + ".spirituality");

                if (pathway == null || acting == 0 || spirituality == 0) {
                    Bukkit.getConsoleSender().sendMessage("Failed to initialize " + s + ": missing attributes");
                    continue;
                }

                Pathway.initializeNew(pathway, UUID.fromString(s), sequence, acting, spirituality);
            } catch (Exception exception) {
                Bukkit.getConsoleSender().sendMessage("Failed to initialize " + s);

                // Error message
                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Bukkit.getConsoleSender().sendMessage("§c" + exceptionAsString);
            }
        }
    }

    private void loadCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect)) {
            log("CoreProtect not found!");
        }

        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            log("CoreProtect not enabled!");
        }

        if (CoreProtect.APIVersion() < 9) {
            log("CoreProtect version outdated!");
        }

        coreProtect = CoreProtect;
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

}
