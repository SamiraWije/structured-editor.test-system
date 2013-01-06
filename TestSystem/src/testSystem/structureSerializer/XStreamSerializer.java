package testSystem.structureSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import ru.ipo.structurededitor.model.DSLBean;
import testSystem.util.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Vladislav Dolbilov (dvladislv@gmail.com)
 */
public class XStreamSerializer {
    private static final Logger log = Logger.getLogger(XStreamSerializer.class.getName());

    private final List<Converter> converters;

    public XStreamSerializer(List<Converter> converters) {
        this.converters = converters;
    }

    public void saveStructure(String fileName, String subSystem, DSLBean bean) {
        final XStream xs = new XStream();
        for (Converter c: converters) {
            xs.registerConverter(c);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(fileName);
            xs.toXML(bean, os);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error on saving to file " + fileName, e);
        } finally {
            IOUtils.closeSilently(os);
        }
    }
}
