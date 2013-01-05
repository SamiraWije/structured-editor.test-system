package testSystem.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class IOUtils {
    public static void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            //do nothing
        }
    }
}
