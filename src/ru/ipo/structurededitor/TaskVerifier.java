package ru.ipo.structurededitor;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Relation;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.structureBuilder.StructureBuilder;
import ru.ipo.structurededitor.testLang.geom.*;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.05.11
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class TaskVerifier {
    private DSLBean bean;
    private String subSystem;
    private Application app;

    public TaskVerifier(DSLBean bean, String subSystem, Application app) {
        this.subSystem=subSystem;
        this.bean=bean;
        this.app=app;
    }

    private boolean geomVerify(){
        Pred[] preds = ((GeoStatement) bean).getPreds();
        for (Pred pred: preds){
           if (pred instanceof GeoPointGeoLineBinPred){
              GeoElement geo1,geo2;
              AbstractGeoPoint point = ((GeoPointGeoLineBinPred) pred).getE1();
              AbstractGeoLine line = ((GeoPointGeoLineBinPred) pred).getE2();
              if (point instanceof GeoPointLink)
                 geo1=((GeoPointLink) point).getGeo();
              else
                 geo1= StructureBuilder.getGeoByCaption(((PointElement) point).getName(),app);
              if (line instanceof GeoLineLink)
                 geo2=((GeoLineLink) line).getGeo();
              else
                 geo2= StructureBuilder.getGeoByCaption(((LineElement) line).getName(),app);
              Relation rel = new Relation(app.getKernel());
              if (geo1==null || geo2==null)
                  return false;

              String relStr= rel.relation(geo1,geo2);
              System.out.println(relStr);
               if (pred instanceof LaysOnPred && relStr.contains("не лежит на"))
                 return false;
           }  else  if (pred instanceof GeoLineBinPred){
              GeoElement geo1,geo2;
              AbstractGeoLine line1 = ((GeoLineBinPred) pred).getE1();
              AbstractGeoLine line2 = ((GeoLineBinPred) pred).getE2();
              if (line1 instanceof GeoLineLink)
                 geo1=((GeoLineLink) line1).getGeo();
              else
                 geo1= StructureBuilder.getGeoByCaption(((LineElement) line1).getName(),app);
              if (line2 instanceof GeoLineLink)
                 geo2=((GeoLineLink) line2).getGeo();
              else
                 geo2= StructureBuilder.getGeoByCaption(((LineElement) line2).getName(),app);
               if (geo1==null || geo2==null)
                   return false;
              Relation rel = new Relation(app.getKernel());
              String relStr= rel.relation(geo1,geo2);
              System.out.println(relStr);
              if (pred instanceof ParallPred && !relStr.contains("параллельны") ||
                  pred instanceof PerpendPred && !relStr.contains("перпендикулярны"))
                  return false;
           }
        }
        return true;
    }
    public boolean verify() {
        if (subSystem.equals("geom"))
            return geomVerify();
        return false;
    }

}
