package dev.ua.ikeepcalm.utils;

import dev.ua.ikeepcalm.LordOfTheMinecraft;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ErrorLoggerUtil {

    private static final String PLUGIN_FOLDER = LordOfTheMinecraft.instance.getDataFolder().getAbsolutePath();
    private static final String ERROR_FOLDER = PLUGIN_FOLDER + File.separator + "errors";
    private static final String ABILITY_LOG_FILE = ERROR_FOLDER + File.separator + "abilities.log";
    private static final String DISASTER_LOG_FILE = ERROR_FOLDER + File.separator + "disasters.log";

    public static void logAbility(Exception e, String abilityName) {
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

    public static void logDisaster(Exception e, String disasterName) {
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

}
