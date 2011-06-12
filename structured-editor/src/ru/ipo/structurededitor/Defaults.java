package ru.ipo.structurededitor;


import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.controller.EditorsRegistryHook;
import ru.ipo.structurededitor.controller.FieldMask;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.comb.*;
import ru.ipo.structurededitor.view.editors.*;

/**
 * Created by IntelliJ IDEA.
 * User: ilya
 * Date: 28.10.2010
 * Time: 1:24:04
 */
public class Defaults {
    public static void registerDefaultBeans(DSLBeansRegistry reg) {
        reg.registerBean(ArrayExpr.class);
        reg.registerBean(BinExpr.class);
        reg.registerBean(CountExaminer.class);
        reg.registerBean(IndexExaminer.class);
        reg.registerBean(ListExaminer.class);
        reg.registerBean(AnswerExaminer.class);
        reg.registerBean(CurElementExpr.class);
        reg.registerBean(DescartesPower.class);
        reg.registerBean(EqExpr.class);
        reg.registerBean(Expr.class);
        reg.registerBean(IntSegment.class);
        reg.registerBean(PrjExpr.class);
        reg.registerBean(Kit.class);
        reg.registerBean(Statement.class);
        reg.registerBean(Examiner.class);
        reg.registerBean(CombKit.class);
        reg.registerBean(LayoutKit.class);
        reg.registerBean(EnumKit.class);
        reg.registerBean(ConstantElement.class);
        reg.registerBean(InnerConstantElement.class);
        reg.registerBean(IntConstantElement.class);
        reg.registerBean(AddExpr.class);
        reg.registerBean(DiffExpr.class);
        reg.registerBean(RemExpr.class);
        reg.registerBean(IntDivExpr.class);
        reg.registerBean(EvExpr.class);
        reg.registerBean(NotEvExpr.class);
        reg.registerBean(LogAndExpr.class);
        reg.registerBean(LogNotExpr.class);
        reg.registerBean(LkExpr.class);
        reg.registerBean(LogOrExpr.class);
        reg.registerBean(GtExpr.class);
        reg.registerBean(SlExpr.class);
        reg.registerBean(ToNumExpr.class);
        reg.registerBean(CalcExpr.class);
        reg.registerBean(CalculableExpr.class);
        reg.registerBean(ModCalculableExpr.class);

    }

    public static void registerDefaultEditors(EditorsRegistry editorsRegistry) {

        editorsRegistry.setDefaultEditor(VoidEditor.class);

        editorsRegistry.registerHook(new EditorsRegistryHook() {
            public Class<? extends FieldEditor> substituteEditor(Class<? extends DSLBean> beanClass,
                                                                 String propertyName, FieldMask mask, Class valueType) {
                if (valueType.isEnum()) {
                    return EnumEditor.class;
                }
                return null;
            }
        });

        editorsRegistry.registerHook(new EditorsRegistryHook() {
            public Class<? extends FieldEditor> substituteEditor(Class<? extends DSLBean> beanClass,
                                                                 String propertyName, FieldMask mask, Class valueType) {
                Class superClass = valueType.getSuperclass();
                while (superClass != null && superClass != Object.class) {
                    valueType = superClass;
                    superClass = superClass.getSuperclass();
                }

                Class[] interf = valueType.getInterfaces();
                if (interf != null && interf.length > 0 && valueType.getInterfaces()[0] == DSLBean.class) {
                    return DSLBeanEditor.class;
                }
                return null;
            }
        });


        editorsRegistry.registerEditor(String.class, StringEditor.class);

        editorsRegistry.registerEditor(int.class, IntEditor.class);
        editorsRegistry.registerEditor(Integer.class, IntEditor.class);

        editorsRegistry.registerEditor(double.class, DoubleEditor.class);
        editorsRegistry.registerEditor(Double.class, DoubleEditor.class);

        editorsRegistry.registerEditor(boolean.class, BooleanEditor.class);
        editorsRegistry.registerEditor(Boolean.class, BooleanEditor.class);

    }

}
