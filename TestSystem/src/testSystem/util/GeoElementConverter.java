package testSystem.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.util.logging.Logger;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class GeoElementConverter implements Converter {
    private static final Logger log = Logger.getLogger(GeoElementConverter.class.getName());

    private final Application app;

    public GeoElementConverter(Application app) {
        this.app = app;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        final GeoElement element = (GeoElement) source;
        writer.setValue(element.getCaption());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final String caption = reader.getValue();
        final GeoElement element = GeogebraUtils.getGeoByCaption(caption, app);
        log.info(String.format("Found [%s] for caption '%s'", element, caption));
        return element;
    }

    @Override
    public boolean canConvert(Class type) {
        return GeoElement.class.isAssignableFrom(type);
    }
}
