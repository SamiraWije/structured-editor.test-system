package testSystem.util;

import java.io.IOException;
import java.util.logging.LogManager;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class LogConfigLoader {
    public static void configureLogger() {
        try {
            LogManager.getLogManager().readConfiguration(
                    LogConfigLoader.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
    }
}
