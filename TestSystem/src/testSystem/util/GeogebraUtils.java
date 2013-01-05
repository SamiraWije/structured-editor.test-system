package testSystem.util;

import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class GeogebraUtils {

    public static GeoElement getGeoByCaption(String caption, Application app) {
        if (caption.matches("[A-Z][A-Z]")) {
            GeoPoint p1 = (GeoPoint) getGeoByCaption(String.valueOf(caption.charAt(0)), app);
            GeoPoint p2 = (GeoPoint) getGeoByCaption(String.valueOf(caption.charAt(1)), app);
            if (p1 != null && p2 != null) {
                GeoSegment gs = new GeoSegment(app.getKernel().getConstruction(), p1, p2);
                gs.calcLength();
                return gs;
            }
        } else if (caption.matches("[A-Z][A-Z][A-Z]")) {
            GeoPoint p1 = (GeoPoint) getGeoByCaption(String.valueOf(caption.charAt(0)), app);
            GeoPoint p2 = (GeoPoint) getGeoByCaption(String.valueOf(caption.charAt(1)), app);
            GeoPoint p3 = (GeoPoint) getGeoByCaption(String.valueOf(caption.charAt(2)), app);

            if (p1 != null && p2 != null && p3 != null) {
                GeoAngle gs = new GeoAngle(app.getKernel().getConstruction());
                double bx, by, vx, vy, wx, wy;
                bx = p2.inhomX;
                by = p2.inhomY;
                vx = p1.inhomX - bx;
                vy = p1.inhomY - by;
                wx = p3.inhomX - bx;
                wy = p3.inhomY - by;
                if (app.getKernel().isZero(vx) && app.getKernel().isZero(vy) ||
                        app.getKernel().isZero(wx) && app.getKernel().isZero(wy)) {
                    gs.setUndefined();
                    return gs;
                }

                double det = vx * wy - vy * wx;
                double prod = vx * wx + vy * wy;
                double value = Math.atan2(det, prod);

                gs.setValue(value);
                return gs;
            }
        }

        for (GeoElement geo : getAllElements(app)) {
            if (geo.getCaption().equals(caption)) {
                return geo;
            }
        }
        return null;
    }

    public static List<GeoElement> getAllElements(Application app) {
        app.selectAll(0);
        final List<Object> selected = new ArrayList<Object>(app.getSelectedGeos());
        app.clearSelectedGeos();

        final List<GeoElement> result = new ArrayList<GeoElement>(selected.size());
        for (Object o: selected) {
            if (o instanceof GeoElement) {
                result.add((GeoElement) o);
            }
        }

        return result;
    }
}
