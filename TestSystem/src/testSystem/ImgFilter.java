package testSystem;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 25.09.2010
 * Time: 14:49:18
 * To change this template use File | Settings | File Templates.
 */

import ru.ipo.structurededitor.view.editors.settings.StringSettings;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImgFilter extends FileFilter {
    public boolean accept(File f) {
        String name = f.getName().toLowerCase();
        return f.isDirectory() || (name.endsWith(".jpg")
                || (name.endsWith(".jpeg"))
                || (name.endsWith(".gif"))
                || (name.endsWith(".png"))
        );
    }

    public String getDescription() {
        return "Файлы изображений";
    }
}
