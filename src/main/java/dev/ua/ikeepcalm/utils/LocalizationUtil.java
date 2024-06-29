package dev.ua.ikeepcalm.utils;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

public class LocalizationUtil {

    public static String getLocalizedString(String key) {
        String s = LordOfTheMinecraft.instance.getLangConfig().getString(key);
        if (s == null) {
            LordOfTheMinecraft.instance.log("Missing key: " + key);
        }

        return s;
    }

    public static String getLocalizedString(String node, String key) {
        ConfigurationSection section = LordOfTheMinecraft.instance.getLangConfig().getConfigurationSection(node);
        if (section == null) {
            LordOfTheMinecraft.instance.log("Missing node: " + node);
            return "Not set! Check console for errors!";
        }

        String s = section.getString(key);
        if (s == null) {
            LordOfTheMinecraft.instance.log("Missing key: " + node+ "."  + key);
            return "Not set! Check console for errors!";
        }

        return s;
    }

    public static String getLocalizedString(String node, String subNode, String key) {
        ConfigurationSection section = LordOfTheMinecraft.instance.getLangConfig().getConfigurationSection(node);
        if (section == null) {
            LordOfTheMinecraft.instance.log("Missing node: " + node);
            return "Not set! Check console for errors!";
        }

        ConfigurationSection subSection = section.getConfigurationSection(subNode);
        if (subSection == null) {
            LordOfTheMinecraft.instance.log("Missing subNode: " + node+ "."  + subNode);
            return "Not set! Check console for errors!";
        }

        String s = subSection.getString(key);
        if (s == null) {
            LordOfTheMinecraft.instance.log("Missing key: " + node + "." + subNode+ "."  + key);
            return "Not set! Check console for errors!";
        }

        return s;
    }

    public static List<String> getLocalizedArray(String node, String subNode, String key) {
        ConfigurationSection section = LordOfTheMinecraft.instance.getLangConfig().getConfigurationSection(node);
        if (section == null) {
            LordOfTheMinecraft.instance.log("Missing node: " + node);
            return Collections.singletonList("Not set! Check console for errors!");
        }

        ConfigurationSection subSection = section.getConfigurationSection(subNode);
        if (subSection == null) {
            LordOfTheMinecraft.instance.log("Missing subNode: " + node+ "."  + subNode);
            return Collections.singletonList("Not set! Check console for errors!");
        }

        return subSection.getStringList(key);
    }

}
