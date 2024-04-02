package dev.ua.ikeepcalm.utils;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

public class LocalizationUtil {

    public static String findLocalizedString(String key) {
        String[] split = key.split("\\.");
        if (split.length == 1) {
            return getLocalizedString(key);
        } else if (split.length == 2) {
            return getLocalizedString(split[0], split[1]);
        } else if (split.length == 3) {
            return getLocalizedString(split[0], split[1], split[2]);
        } else {
            LordOfTheMinecraft.instance.log("Key longer than 3 nodes: " + key);
            return "Not set! Check console for errors!";
        }
    }

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

    public static List<String> getLocalizedArray(String key) {
        List<String> s = LordOfTheMinecraft.instance.getLangConfig().getStringList(key);
        if (s == null) {
            LordOfTheMinecraft.instance.log("Missing key: " + key);
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

        List<String> s = subSection.getStringList(key);
        if (s == null) {
            LordOfTheMinecraft.instance.log("Missing key: " + node + "." + subNode+ "."  + key);
            return Collections.singletonList("Not set! Check console for errors!");
        }

        return s;
    }

}
