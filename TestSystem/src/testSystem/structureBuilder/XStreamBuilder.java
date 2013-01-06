package testSystem.structureBuilder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import ru.ipo.structurededitor.model.DSLBean;
import testSystem.util.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class XStreamBuilder {
    private static final Logger log = Logger.getLogger(XStreamBuilder.class.getName());

    private final List<Converter> converters;

    public XStreamBuilder(List<Converter> converters) {
        this.converters = converters;
    }

    public DSLBean getStructure(String filename) {
        final XStream xs = new XStream();
        for (Converter c : converters) {
            xs.registerConverter(c);
        }

        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            return (DSLBean) xs.fromXML(is);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error on loading from " + filename, e);
            return null;
        } finally {
            IOUtils.closeSilently(is);
        }
    }
}
