package dev.ua.ikeepcalm.optional.emporium.managers;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.optional.emporium.Advertiser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdvertiserManager {

    private final File configFile;
    private final FileConfiguration config;

    public AdvertiserManager() {
        configFile = new File(LordOfTheMinecraft.instance.getDataFolder(), "advertisers.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveAdvertiser(Advertiser advertiser) {
        config.set("advertisers." + advertiser.getNickname(), advertiser.getBalance());
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Advertiser findByNickname(String nickname) {
        if (config.contains("advertisers." + nickname)) {
            double balance = config.getDouble("advertisers." + nickname);
            return new Advertiser(nickname, balance);
        }
        return null;
    }

    public boolean existsByNickname(String nickname) {
        return config.contains("advertisers." + nickname);
    }

    public Map<String, Double> getAllAdvertisers() {
        Map<String, Double> advertisers = new HashMap<>();
        if (config.contains("advertisers")) {
            for (String key : config.getConfigurationSection("advertisers").getKeys(false)) {
                advertisers.put(key, config.getDouble("advertisers." + key));
            }
        }
        return advertisers;
    }
}
