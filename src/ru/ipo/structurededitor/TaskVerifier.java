package ru.ipo.structurededitor;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Relation;
import geogebra.main.Application;
import org.mathpiper.builtin.Array;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.structureBuilder.StructureBuilder;
import ru.ipo.structurededitor.testLang.comb.*;
import ru.ipo.structurededitor.testLang.geom.*;
import ru.ipo.structurededitor.testLang.logic.*;
import ru.ipo.structurededitor.view.editors.ArrayEditor;

import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.05.11
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class TaskVerifier {
    private DSLBean bean, ans;
    private String subSystem;
    private Application app;
    private HashMap<String, Boolean> varValues;
    private boolean formAnswer = false;

    public TaskVerifier(DSLBean bean, String subSystem, Application app, DSLBean ans) {
        this.subSystem = subSystem;
        this.bean = bean;
        if (subSystem.equals("geom")) {
            this.app = app;
        } else if (subSystem.equals("log")) {
            this.ans = ans;
            prepareVarValues((LogicAnswer) ans);
        }
    }

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
                if (geo1 == null || geo2 == null)
                    return false;

                String relStr = rel.relation(geo1, geo2);
                System.out.println(relStr);
                if (pred instanceof LaysOnPred && relStr.contains("не лежит на"))
                    return false;
            } else if (pred instanceof GeoLineBinPred) {
                GeoElement geo1, geo2;
                AbstractGeoLine line1 = ((GeoLineBinPred) pred).getE1();
                AbstractGeoLine line2 = ((GeoLineBinPred) pred).getE2();
                if (line1 instanceof GeoLineLink)
                    geo1 = ((GeoLineLink) line1).getGeo();
                else
                    geo1 = StructureBuilder.getGeoByCaption(((LineElement) line1).getName(), app);
                if (line2 instanceof GeoLineLink)
                    geo2 = ((GeoLineLink) line2).getGeo();
                else
                    geo2 = StructureBuilder.getGeoByCaption(((LineElement) line2).getName(), app);
                if (geo1 == null || geo2 == null)
                    return false;
                Relation rel = new Relation(app.getKernel());
                String relStr = rel.relation(geo1, geo2);
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
        else if (subSystem.equals("log"))
            return logVerify();
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
                    res = parse(expr1) && res ;
                } else if (expr instanceof LogOrExpr) {
                    res = parse(expr1) || res;
                }
            }
            return res;
        } else if (expr instanceof LogNotExpr) {
            return !parse(expr);
        } else if (expr instanceof LogicAtom) {
            if (formAnswer) {
               Object arr = ((LogicAnswer) ans).getAnswer();
               int size = java.lang.reflect.Array.getLength(arr);
               arr= ArrayEditor.resizeArray(arr,size+1);
               LogicAtomValue item = new LogicAtomValue();
               item.setName(((LogicAtom) expr).getVal());
               item.setVal(false);
               java.lang.reflect.Array.set(arr,size,item);
               ((LogicAnswer) ans).setAnswer((LogicAtomValue[])arr);
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
        if (lc==null)
            return false;
        Expr[] exprs = lc.getItems();
        int num = 0;
        boolean example;
        if (lc instanceof TrueLogicCondition) {
            example = true;
        } else {
            example = false;
        }
        for (Expr expr : exprs) {
            if (parse(expr) == example)
                num++;
        }
        return lc.getNum() == num;
    }


}
