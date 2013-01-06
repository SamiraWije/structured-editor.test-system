package testSystem.util;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class IOUtils {
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());

    public static void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            //do nothing
        }
    }

    public static boolean copyFile(String from, String to) {
        try {
            final InputStream in = new FileInputStream(from);
            final OutputStream out = new FileOutputStream(to);

            pump(in, out, true);

            log.log(Level.INFO, String.format("File [%s] copied to [%s]", from, to));
            return true;
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Failed to copy file " + from, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to copy file " + from, e);
        }
        return false;
    }

    public static void pump(InputStream is, OutputStream os) throws IOException {
        pump(is, os, true);
    }

    public static void pump(InputStream is, OutputStream os, boolean closeStreams) throws IOException {
        try {
            byte[] buf = new byte[32 * 1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } finally {
            if (closeStreams) {
                closeSilently(is);
                closeSilently(os);
            }
        }
    }
}
