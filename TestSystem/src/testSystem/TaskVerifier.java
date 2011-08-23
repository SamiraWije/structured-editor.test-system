package testSystem;

import geogebra.kernel.*;
import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBean;
import testSystem.lang.comb.*;
import testSystem.lang.logic.*;
import testSystem.lang.geom.*;
import testSystem.structureBuilder.StructureBuilder;

import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.05.11
 * Time: 12:00
 */
public class TaskVerifier {
    private DSLBean bean, ans;
    private String subSystem;
    private Application app;
    private HashMap<String, Boolean> varValues;
    private boolean formAnswer = false;
    private Examiner ver;
    private int count;

    public TaskVerifier(DSLBean bean, String subSystem, Application app, DSLBean ans, String combAns) {
        this.subSystem = subSystem;
        this.bean = bean;
        if (subSystem.equals("geom")) {
            this.app = app;
        } else if (subSystem.equals("log")) {
            this.ans = ans;
            prepareVarValues((LogicAnswer) ans);
        } else if (subSystem.equals("comb")) {
            this.combAns = combAns;
        }
    }

    private String combAns;

    public void makeLogAnswer() {
        formAnswer = true;
        ((LogicAnswer) ans).setAnswer(new LogicAtomValue[0]);
        logVerify();
        formAnswer = false;

    }

    private void prepareVarValues(LogicAnswer param) {
        varValues = new HashMap<String, Boolean>();
        LogicAtomValue[] answer = param.getAnswer();
        if (answer != null && answer.length > 0)
            for (LogicAtomValue item : answer) {
                if (item != null)
                    varValues.put(item.getName(), item.getVal());
            }
    }

    private boolean geomVerify() {
        Pred[] preds = ((GeoStatement) bean).getPreds();
        for (Pred pred : preds) {
            if (pred instanceof GeoPointGeoLineBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoPoint point = ((GeoPointGeoLineBinPred) pred).getE1();
                AbstractGeoLine line = ((GeoPointGeoLineBinPred) pred).getE2();
                if (point instanceof GeoPointLink)
                    geo1 = ((GeoPointLink) point).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((PointElement) point).getName(), app);
                if (line instanceof GeoLineLink)
                    geo2 = ((GeoLineLink) line).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((LineElement) line).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoPoint) || !(geo2 instanceof GeoLine))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);
                System.out.println(relStr);
                if (pred instanceof LaysOnPred && relStr.contains("не лежит на"))
                    return false;
            } else if (pred instanceof GeoSegLineBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoSegLine line1 = ((GeoSegLineBinPred) pred).getE1();
                AbstractGeoSegLine line2 = ((GeoSegLineBinPred) pred).getE2();
                if (line1 instanceof AbstractGeoLine){
                    if (line1 instanceof GeoLineLink)
                        geo1 = ((GeoLineLink) line1).getGeo();
                    else
                        geo1 = StructureBuilder.getGeoByCaption(((LineElement) line1).getName(), app);
                } else {
                    if (line1 instanceof GeoSegmentLink)
                        geo1 = ((GeoSegmentLink) line1).getGeo();
                    else
                        geo1 = StructureBuilder.getGeoByCaption(((SegmentElement) line1).getName(), app);
                   if (geo1 instanceof GeoSegment){
                    GeoLine geoLine1=new GeoLine(app.getKernel().getConstruction());
                    GeoVec3D.lineThroughPoints(((GeoSegment)geo1).getStartPoint(),((GeoSegment)geo1).getEndPoint(),
                            geoLine1);
                    geo1 = geoLine1;
                   }
                }
                if (line2 instanceof AbstractGeoLine){
                    if (line2 instanceof GeoLineLink)
                        geo2 = ((GeoLineLink) line1).getGeo();
                    else
                        geo2 = StructureBuilder.getGeoByCaption(((LineElement) line2).getName(), app);
                } else {
                    if (line2 instanceof GeoSegmentLink)
                        geo2 = ((GeoSegmentLink) line2).getGeo();
                    else
                        geo2 = StructureBuilder.getGeoByCaption(((SegmentElement) line2).getName(), app);
                   if (geo2 instanceof GeoSegment){
                        GeoLine geoLine2=new GeoLine(app.getKernel().getConstruction());
                        GeoVec3D.lineThroughPoints(((GeoSegment)geo2).getStartPoint(),((GeoSegment)geo2).getEndPoint(),
                            geoLine2);
                        geo2 = geoLine2;
                   }

                }
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoLine) || !(geo2 instanceof GeoLine))
                    return false;
                Relation rel = new Relation(app.getKernel());
                String relStr = rel.relation(geo1, geo2);
                //rel.relation(new GeoSegment(app.getKernel().getConstruction()))
                System.out.println(relStr);
                if (pred instanceof ParallPred && !relStr.contains("параллельны") ||
                        pred instanceof PerpendPred && !relStr.contains("перпендикулярны"))
                    return false;
            } else if (pred instanceof GeoPointGeoSegmentBinPred) {
                GeoElement geo1, geo2, geo3, geo4;
                AbstractGeoPoint point = ((GeoPointGeoSegmentBinPred) pred).getE1();
                AbstractGeoSegment seg = ((GeoPointGeoSegmentBinPred) pred).getE2();
                if (point instanceof GeoPointLink)
                    geo1 = ((GeoPointLink) point).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((PointElement) point).getName(), app);
                if (seg instanceof GeoSegmentLink)
                    geo2 = ((GeoSegmentLink) seg).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((SegmentElement) seg).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoPoint) || !(geo2 instanceof GeoSegment))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);
                GeoPoint p1 = (GeoPoint) ((GeoSegment) geo2).getStartPointAsGeoElement();
                GeoPoint p2 = (GeoPoint) ((GeoSegment) geo2).getEndPointAsGeoElement();
                GeoSegment s1 = new GeoSegment(app.getKernel().getConstruction(), p1, (GeoPoint) geo1);
                GeoSegment s2 = new GeoSegment(app.getKernel().getConstruction(), p2, (GeoPoint) geo1);
                s1.calcLength();
                s2.calcLength();
                System.out.println(relStr);
                if (pred instanceof MidpointPred && (Math.round(s1.getLength() * 100) != Math.round(s2.getLength() * 100)
                        || relStr.contains("не лежит на")) ||
                        pred instanceof LaysOnSegmentPred && relStr.contains("не лежит на"))
                    return false;
            } else if (pred instanceof GeoSegmentBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoSegment seg1 = ((GeoSegmentBinPred) pred).getE1();
                AbstractGeoSegment seg2 = ((GeoSegmentBinPred) pred).getE2();
                if (seg1 instanceof GeoSegmentLink)
                    geo1 = ((GeoSegmentLink) seg1).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((SegmentElement) seg1).getName(), app);
                if (seg2 instanceof GeoSegmentLink)
                    geo2 = ((GeoSegmentLink) seg2).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((SegmentElement) seg2).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoSegment) || !(geo2 instanceof GeoSegment))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);


                System.out.println(relStr);
                if (pred instanceof SegEqualPred &&
                        relStr.contains("не равны"))
                        //&& (Math.round(((GeoSegment) geo1).getLength() * 100) !=
                        //Math.round(((GeoSegment) geo2).getLength() * 100)))
                    return false;
            } else if (pred instanceof GeoAngleBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoAngle seg1 = ((GeoAngleBinPred) pred).getE1();
                AbstractGeoAngle seg2 = ((GeoAngleBinPred) pred).getE2();
                if (seg1 instanceof GeoAngleLink)
                    geo1 = ((GeoAngleLink) seg1).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((AngleElement) seg1).getName(), app);
                if (seg2 instanceof GeoAngleLink)
                    geo2 = ((GeoAngleLink) seg2).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((AngleElement) seg2).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoAngle) || !(geo2 instanceof GeoAngle))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);

                System.out.println(relStr);
                if (pred instanceof AngleEqualPred &&
                     relStr.contains("не идентичны"))

                        //((GeoAngle) geo1).getRawAngle() * 100) !=
                        //Math.round(((GeoAngle) geo2).getRawAngle() * 100)))
                    return false;
            } else if (pred instanceof GeoCircleBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoCircle seg1 = ((GeoCircleBinPred) pred).getE1();
                AbstractGeoCircle seg2 = ((GeoCircleBinPred) pred).getE2();
                if (seg1 instanceof GeoCircleLink)
                    geo1 = ((GeoCircleLink) seg1).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((CircleElement) seg1).getName(), app);
                if (seg2 instanceof GeoCircleLink)
                    geo2 = ((GeoCircleLink) seg2).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((CircleElement) seg2).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo1 instanceof GeoConic) || !(geo2 instanceof GeoConic))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);


                System.out.println(relStr);
                if (pred instanceof CircleTangentPred && relStr.contains("пересекается")) //Не работает
                    return false;
            } else if (pred instanceof GeoLineGeoCircleBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoLine seg1 = ((GeoLineGeoCircleBinPred) pred).getE1();
                AbstractGeoCircle seg2 = ((GeoLineGeoCircleBinPred) pred).getE2();
                if (seg1 instanceof GeoLineLink)
                    geo1 = ((GeoLineLink) seg1).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((LineElement) seg1).getName(), app);
                if (seg2 instanceof GeoCircleLink)
                    geo2 = ((GeoCircleLink) seg2).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((CircleElement) seg2).getName(), app);
                Relation rel = new Relation(app.getKernel());
                if (geo1 == null || geo2 == null || !(geo2 instanceof GeoConic) || !(geo1 instanceof GeoLine))
                    return false;
                //GeoAngle s = new GeoAngle(app.getKernel().getConstruction());

                String relStr = rel.relation(geo1, geo2);


                System.out.println(relStr);
                if (pred instanceof LineCircleTangentPred && relStr.contains("пересекается"))
                    return false;
            } else if (pred instanceof ValuePred) {
                GeoElement geo;
                double value = ((ValuePred) pred).getValue();
                if (pred instanceof SegmentValuePred) {
                    AbstractGeoSegment seg = ((SegmentValuePred) pred).getE();
                    if (seg instanceof GeoSegmentLink)
                        geo = ((GeoSegmentLink) seg).getGeo();
                    else
                        geo = StructureBuilder.getGeoByCaption(((SegmentElement) seg).getName(), app);
                    if (geo==null || !(geo instanceof GeoSegment))
                        return false;
                    return Math.round(((GeoSegment)geo).getLength()*100)==Math.round(value*100);

                } else if (pred instanceof AngleValuePred) {
                    AbstractGeoAngle seg = ((AngleValuePred) pred).getE();
                    if (seg instanceof GeoAngleLink)
                        geo = ((GeoAngleLink) seg).getGeo();
                    else
                        geo = StructureBuilder.getGeoByCaption(((AngleElement) seg).getName(), app);
                    if (geo==null || !(geo instanceof GeoAngle))
                        return false;
                    return Math.round(((GeoAngle)geo).getRawAngle()/Math.PI*1800)==Math.round(value*10);
                }

            }
        }
        return true;
    }

    private boolean processKit(Kit kit) {
        if (kit instanceof IntSegment) {
            int from = ((IntSegment) kit).getFrom();
            int to = ((IntSegment) kit).getTo();
            for (int i = from; i <= to; i++) {
                if (ver instanceof CountExaminer) {
                    /*if ((boolean) combParse(((CountExaminer) ver).getExpr()))
                    count++;*/
                }
            }
        }
        return false;
    }

    private boolean combVerify() {
        ver = ((Statement) bean).getExaminer();
        Kit kit = ((Statement) bean).getKit();
        return processKit(kit);
    }

    public boolean verify() {
        if (subSystem.equals("geom"))
            return geomVerify();
        else if (subSystem.equals("log"))
            return logVerify();
        else if (subSystem.equals("comb"))
            return combVerify();
        return false;
    }

    private boolean parse(Expr expr) {
        if (expr instanceof ArrayExpr) {
            Expr[] exprs = ((ArrayExpr) expr).getItems();
            boolean res = true, flag = true;
            for (Expr expr1 : exprs) {
                if (flag) {
                    res = parse(expr1);
                    flag = false;
                } else if (expr instanceof LogAndExpr) {
                    res = parse(expr1) && res;
                } else if (expr instanceof LogOrExpr) {
                    res = parse(expr1) || res;
                }
            }
            return res;
        } else if (expr instanceof LogNotExpr) {
            return !parse(((LogNotExpr) expr).getExpr());
        } else if (expr instanceof LogicAtom) {
            if (formAnswer) {
                Object arr = ((LogicAnswer) ans).getAnswer();
                String name = ((LogicAtom) expr).getVal();
                boolean flag = true;
                for (LogicAtomValue atomValue : (LogicAtomValue[]) arr) {
                    if (atomValue.getName().equals(name)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    int size = java.lang.reflect.Array.getLength(arr);
                    arr = StructureBuilder.resizeArray(arr, size + 1);
                    LogicAtomValue item = new LogicAtomValue();
                    item.setName(name);
                    item.setVal(false);
                    java.lang.reflect.Array.set(arr, size, item);
                    ((LogicAnswer) ans).setAnswer((LogicAtomValue[]) arr);
                }
            } else {
                Boolean val = varValues.get(((LogicAtom) expr).getVal());
                if (val != null)
                    return val;
                else
                    return false;
            }
        }
        return false;
    }

    private boolean logVerify() {
        LogicCondition lc = ((LogicStatement) bean).getCondition();
        if (lc == null)
            return false;
        Expr[] exprs = lc.getItems();
        int num = 0;
        boolean example;
        example = lc instanceof TrueLogicCondition;
        for (Expr expr : exprs) {
            if (parse(expr) == example)
                num++;
        }
        return lc.getNum() == num;
    }


}
