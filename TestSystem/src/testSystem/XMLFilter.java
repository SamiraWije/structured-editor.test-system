package testSystem;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 25.09.2010
 * Time: 14:49:18
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class XMLFilter extends FileFilter {
    public boolean accept(File f) {
        return f.isDirectory() || (f.getName().endsWith(".xml"));
    }

    public String getDescription() {
        return "XML-file";
    }
}
