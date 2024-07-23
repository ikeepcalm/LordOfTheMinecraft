package dev.ua.ikeepcalm.utils;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class LoggerUtil {

    private static final String PLUGIN_FOLDER = LordOfTheMinecraft.instance.getDataFolder().getAbsolutePath();
    private static final String ERROR_FOLDER = PLUGIN_FOLDER + File.separator + "errors";
    private static final String PLAYERS_FOLDER = PLUGIN_FOLDER + File.separator + "players";

    private static final String ABILITY_LOG_FILE = ERROR_FOLDER + File.separator + "abilities.log";
    private static final String DISASTER_LOG_FILE = ERROR_FOLDER + File.separator + "disasters.log";

    public static void logAbilityError(Exception e, String abilityName) {
        File errorDir = new File(ERROR_FOLDER);
        if (!errorDir.exists()) {
            errorDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        String logMessage = String.format("[%s] [%s] Exception: %s%nStackTrace:%n%s", currentDateTime, abilityName, e.toString(), stackTrace);
        Logger logger = LordOfTheMinecraft.instance.getLogger();
        logger.severe(logMessage);
        try (FileWriter fw = new FileWriter(ABILITY_LOG_FILE, true); PrintWriter logWriter = new PrintWriter(fw)) {
            logWriter.println(logMessage);
        } catch (IOException ioException) {
            logger.severe("Failed to write to log file");
        }
    }

    public static void logDisasterError(Exception e, String disasterName) {
        File errorDir = new File(ERROR_FOLDER);
        if (!errorDir.exists()) {
            errorDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        String logMessage = String.format("[%s] [%s] Exception: %s%nStackTrace:%n%s", currentDateTime, disasterName, e.toString(), stackTrace);
        Logger logger = LordOfTheMinecraft.instance.getLogger();
        logger.severe(logMessage);
        try (FileWriter fw = new FileWriter(DISASTER_LOG_FILE, true); PrintWriter logWriter = new PrintWriter(fw)) {
            logWriter.println(logMessage);
        } catch (IOException ioException) {
            logger.severe("Failed to write to log file");
        }
    }

    public static void logPlayerAbility(Player player, String abilityName, int spirituality, int maxSpirituality) {
        File playerDir = new File(PLAYERS_FOLDER);
        String playerFile = PLAYERS_FOLDER + File.separator + player.getName() + ".log";

        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        String world = player.getWorld().getName();
        String coordinates = String.format("X: %d, Y: %d, Z: %d", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

        String logMessage = String.format("Ability usage (%s): [%s] - [%s / %s] | at [%s] [%s] ", currentDateTime, abilityName, spirituality, maxSpirituality, world, coordinates);
        Logger logger = LordOfTheMinecraft.instance.getLogger();
        try (FileWriter fw = new FileWriter(playerFile, true); PrintWriter logWriter = new PrintWriter(fw)) {
            logWriter.println(logMessage);
        } catch (IOException ioException) {
            logger.severe("Failed to write to log file");
        }
    }

    public static void logPlayerPotion(Player player, Beyonder beyonder, int sequence, String pathway, boolean successful) {
        File playerDir = new File(PLAYERS_FOLDER);
        String playerFile = PLAYERS_FOLDER + File.separator + player.getName() + ".log";

        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        String actingValue = (int) beyonder.getActingProgress() + " / " + (int) beyonder.getActingNeeded();

        String logMessage;
        if (beyonder.getPathway().getSequence().getCurrentSequence() - 1 == sequence) {
            logMessage = String.format("Repeated potion usage (%s): [%s] lvl - [%s] | acting -> [%s]", currentDateTime, sequence, pathway, actingValue);
        } else {
            logMessage = String.format("Advance potion usage (%s): [%s] lvl - [%s] | acting -> [%s]", currentDateTime, sequence, pathway, actingValue);
        }

        if (!successful) {
            logMessage = logMessage + " | [FAILED]";
        }

        Logger logger = LordOfTheMinecraft.instance.getLogger();
        try (FileWriter fw = new FileWriter(playerFile, true); PrintWriter logWriter = new PrintWriter(fw)) {
            logWriter.println(logMessage);
        } catch (IOException ioException) {
            logger.severe("Failed to write to log file");
        }

    }

    public static void logPlayerLooseControl(Player player, Beyonder beyonder, boolean survival) {
        File playerDir = new File(PLAYERS_FOLDER);
        String playerFile = PLAYERS_FOLDER + File.separator + player.getName() + ".log";

        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(new Date());
        String actingValue = (int) beyonder.getActingProgress() + " / " + (int) beyonder.getActingNeeded();
        String sprititualityValue = (int) beyonder.getSpirituality() + " / " + (int) beyonder.getMaxSpirituality();

        String logMessage = String.format("Lost control (%s): survives -> [%s] / 100; acting -> [%s]; spritituality -> [%s]", currentDateTime, survival, actingValue, sprititualityValue);

        Logger logger = LordOfTheMinecraft.instance.getLogger();
        try (FileWriter fw = new FileWriter(playerFile, true); PrintWriter logWriter = new PrintWriter(fw)) {
            logWriter.println(logMessage);
        } catch (IOException ioException) {
            logger.severe("Failed to write to log file");
        }
    }

}
