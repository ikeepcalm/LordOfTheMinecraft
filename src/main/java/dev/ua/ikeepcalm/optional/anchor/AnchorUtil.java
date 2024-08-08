package dev.ua.ikeepcalm.optional.anchor;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class AnchorUtil {

    public static FileConfiguration getCustomConfig(String fileName) {
        File file = new File(LordOfTheMinecraft.instance.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void saveCustomConfig(String fileName, FileConfiguration config) {
        try {
            File file = new File(LordOfTheMinecraft.instance.getDataFolder(), fileName);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
