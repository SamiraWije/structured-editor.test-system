package testSystem.lang.geom;

import geogebra.kernel.AlgoIntersectLineConic;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoLine;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBean;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.03.11
 * Time: 15:28
 */
public abstract class Pred implements DSLBean {

        public abstract boolean verify(Application app);
}
