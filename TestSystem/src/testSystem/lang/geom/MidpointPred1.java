package testSystem.lang.geom;

import geogebra.main.Application;
import ru.ipo.structurededitor.model.*;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:25:07
 */
@DSLBeanParams(shortcut = "сер |", description = "Середина отрезка")
public class MidpointPred1 extends Pred {
    private static final Logger log = Logger.getLogger(MidpointPred1.class.getName());

    public AbstractGeoPoint getE1() {
        return e1;
    }

    public void setE1(AbstractGeoPoint e1) {
        this.e1 = e1;
    }

    public AbstractGeoPoint getE2() {
        return e2;
    }

    public void setE2(AbstractGeoPoint e2) {
        this.e2 = e2;
    }

    public AbstractGeoPoint getE3() {
        return e3;
    }

    public void setE3(AbstractGeoPoint e3) {
        this.e3 = e3;
    }

    private AbstractGeoPoint e1;
     private AbstractGeoPoint e2;
     private AbstractGeoPoint e3;

    public Cell getLayout() {
            return new Horiz(new FieldCell("e1"), new ConstantCell("середина ["),
                    new FieldCell("e2"), new ConstantCell(","), new FieldCell("e3"),
                    new ConstantCell("]"));
    }

    @Override
    public boolean verify(Application app) {
        log.severe("MidpointPred1 is not implemented");
        return false;
    }
}
