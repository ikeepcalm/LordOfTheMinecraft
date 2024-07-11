package dev.ua.ikeepcalm.optional.emporium.managers;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.optional.emporium.wrappers.ItemWrapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmporiumManager {
    private final File configFile;
    private final FileConfiguration config;

    public EmporiumManager() {
        configFile = new File(LordOfTheMinecraft.instance.getDataFolder(), "emporium.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void initializeEmporium() {
        List<String> items = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            String type;
            switch (random.nextInt(3)) {
                case 0 -> type = "CharWrapper";
                case 1 -> type = "PotionWrapper";
                case 2 -> type = "RecipeWrapper";
                default -> throw new IllegalStateException("Unexpected value: " + random.nextInt(3));
            }
            int pathway = random.nextInt(4);
            int sequence = new Random().nextInt(9) + 1;
            items.add(type + "," + pathway + "," + sequence);
        }
        config.set("emporium.items", items);
        saveConfig();
    }

    public List<ItemWrapper> getEmporiumItems() {
        List<ItemWrapper> itemInfos = new ArrayList<>();
        List<String> items = config.getStringList("emporium.items");
        for (String item : items) {
            String[] parts = item.split(",");
            if (parts.length == 3) {
                itemInfos.add(new ItemWrapper(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        }
        return itemInfos;
    }
    
    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
