package testSystem;

import geogebra.main.Application;
import ru.ipo.structurededitor.model.DSLBean;
import testSystem.lang.DSP.DSPAnswer;
import testSystem.lang.comb.*;
import testSystem.lang.geom.GeoStatement;
import testSystem.lang.geom.Pred;
import testSystem.lang.logic.*;
import testSystem.util.ArrayUtils;

import java.util.HashMap;
import java.util.logging.Logger;

//import matlabcontrol.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 12.05.11
 * Time: 12:00
 */
public class TaskVerifier {
    private static final Logger log = Logger.getLogger(TaskVerifier.class.getName());

    private DSLBean bean, ans;
    private String dspSolution;
    private String subSystem;
    private Application app;
    private HashMap<String, Boolean> varValues;
    private boolean formAnswer = false;
    private Examiner ver;
    private int count;
    public final String SOLUTION_MACROS="Решение;";

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
        } else if (subSystem.equals("DSP")) {
            this.dspSolution=((DSPAnswer)ans).getAnswerMATLAB();
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
        final Pred[] preds = ((GeoStatement) bean).getPreds();

        for (Pred pred : preds) {
            if (pred != null && !pred.verify(app)) {
                return false;
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

    private boolean dspVerify(){
    /*   String verFunc = ((DSPStatement)bean).getVerifier();
       String funcName= verFunc.substring(verFunc.indexOf('=')+1,verFunc.indexOf('('));
       funcName=funcName.trim();
       String funcFileName = funcName+".m";
       verFunc = verFunc.replaceAll(SOLUTION_MACROS,dspSolution);
       System.out.println(verFunc);
       File funcFile=null;
        try {
            String tmpDir = System.getenv("TEMP");
            String functFullFileName=tmpDir+"\\"+funcFileName;
            funcFile=new File(functFullFileName);
            System.out.println("Temporary m-file name: "+functFullFileName);
            FileOutputStream fOut = new FileOutputStream(funcFile);
            fOut.write(verFunc.getBytes());
            fOut.close();
            MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
                .setUsePreviouslyControlledSession(true).build();

            //Create a proxy, which we will use to control MATLAB
            MatlabProxyFactory factory = new MatlabProxyFactory(options);
            MatlabProxy proxy = factory.getProxy();
            proxy.eval("cd "+tmpDir);
            Object[] res = proxy.returningFeval(funcName,1);
            proxy.disconnect();
            funcFile.delete();
            boolean bVal=((boolean [])(res[0]))[0];
            System.out.print("Verifier has returned: "+Boolean.toString(bVal));
            return bVal;
        } catch (IOException e) {
            System.out.println("Functional m-file writing error! "+e);
        } catch (MatlabConnectionException e){
            System.out.println("Matlab connection error: "+e);
        } catch (MatlabInvocationException e){
            System.out.println("Matlab invocation error: "+e);
        } catch (ClassCastException e){
            System.out.println("Verifier function must return 0 or 1! "+e);
        }

      */
       return false;
    }
    public boolean verify() {
        if (subSystem.equals("geom"))
            return geomVerify();
        else if (subSystem.equals("log"))
            return logVerify();
        else if (subSystem.equals("comb"))
            return combVerify();
        else if (subSystem.equals("DSP"))
            return dspVerify();
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
        } else if (expr instanceof BinExpr) {
            Boolean pe1 = parse(((BinExpr) expr).getE1());
            Boolean pe2 = parse(((BinExpr) expr).getE2());
            if (expr instanceof LogEquivExpr){
                return (pe1 && pe2) || (!pe1 && !pe2);
            } else if (expr instanceof LogImplExpr){
                return !pe1 || pe2;
            } else
                return false;
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
                    arr = ArrayUtils.resizeArray(arr, size + 1);
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
