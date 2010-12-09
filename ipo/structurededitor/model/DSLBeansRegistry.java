package ru.ipo.structurededitor.model;

import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.view.editors.AbstractDSLBeanEditor;
import ru.ipo.structurededitor.view.editors.DSLBeanEditor;
import ru.ipo.structurededitor.view.editors.FieldEditor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:38:04
 */
public class DSLBeansRegistry {

    private ArrayList<Class<? extends DSLBean>> beans = new ArrayList<Class<? extends DSLBean>>();
    private static DSLBeansRegistry instance = new DSLBeansRegistry();

    public static DSLBeansRegistry getInstance() {
        return instance;
    }

    private DSLBeansRegistry() {
    }

    public void registerBean(Class<? extends DSLBean> bean) {
        beans.add(bean);

        boolean isAbstract = Modifier.isAbstract(bean.getModifiers());
        EditorsRegistry<FieldEditor> editorEditorsRegistry = EditorsRegistry.getInstance(FieldEditor.class);

        if (isAbstract)
            editorEditorsRegistry.registerEditor(bean, AbstractDSLBeanEditor.class);
        else
            editorEditorsRegistry.registerEditor(bean, DSLBeanEditor.class);
    }

    public List<Class<? extends DSLBean>> getAllSubclasses(Class<? extends DSLBean> bean, boolean onlyNonAbstract) {
        ArrayList<Class<? extends DSLBean>> res = new ArrayList<Class<? extends DSLBean>>();

        for (Class<? extends DSLBean> bc : beans) {
            boolean isAbstract = Modifier.isAbstract(bc.getModifiers());
            if (bean.isAssignableFrom(bc) && (!onlyNonAbstract || !isAbstract))
                res.add(bc);
        }

        return res;
    }

    public Class<? extends DSLBean> getBeanByName(String beanName) {
        for (Class<? extends DSLBean> bc : beans) {
            boolean isAbstract = Modifier.isAbstract(bc.getModifiers());
            if (bc.getSimpleName().equals(beanName))
                return bc;
        }

        return null;
    }
}