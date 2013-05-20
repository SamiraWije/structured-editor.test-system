package testSystem.lang.geom;

import geogebra.kernel.*;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBeanParams;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:12
 */
@DSLBeanParams(shortcut = "/ кас ∘", description = "Линия касается окружности")
public class LineCircleTangentPred extends GeoSegLineGeoCircleBinPred {
    private static final Logger log = Logger.getLogger(LineCircleTangentPred.class.getName());

    @Override
    public void init() {
        op = "касается";
        vert = false;
    }

    @Override
    public boolean verify(Application app) {
        final GeoLine line = getE1().resolveLine(app);
        final GeoConic circle = getE2().resolve(app);
        GeoPoint points[]=new GeoPoint[2];
        points[0]=new GeoPoint(app.getKernel().getConstruction());
        points[1]=new GeoPoint(app.getKernel().getConstruction());
        if (line == null || circle == null) {
            return false;
        }

        /*final Relation rel = new Relation(app.getKernel());
        final String relStr = rel.relation(line, circle);

        log.info(relStr);

        return !(relStr.contains("пересекается")); */
        boolean res = false;
        try {
            Method m = AlgoIntersectLineConic.class.getDeclaredMethod("intersectLineConic",  new Class[]{GeoLine.class,
                                GeoConic.class,points.getClass()});
            m.setAccessible(true);

            res =  (m.invoke(null,new Object[]{line, circle, points}).
                    equals(AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE));
        } catch (NoSuchMethodException e){
                   log.severe("GeoGebra class AlgoIntersectLineConic isn't what it means )) "+e);
                   e.printStackTrace();
        } catch (InvocationTargetException e) {
            log.severe("GeoGebra class AlgoIntersectLineConic isn't what it means )) "+e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            log.severe("GeoGebra class AlgoIntersectLineConic isn't what it means )) "+e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return res;
    }
}
