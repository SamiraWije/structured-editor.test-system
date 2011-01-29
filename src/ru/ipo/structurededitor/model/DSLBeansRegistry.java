package ru.ipo.structurededitor.model;

import ru.ipo.structurededitor.Defaults;
import ru.ipo.structurededitor.controller.EditorsRegistry;
import ru.ipo.structurededitor.view.editors.DSLBeanEditor;

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
    //private static DSLBeansRegistry instance = new DSLBeansRegistry();

    /*public static DSLBeansRegistry getInstance() {
        return instance;
    } */

    public DSLBeansRegistry() {
        Defaults.registerDefaultBeans(this);
    }

    public void registerBean(Class<? extends DSLBean> bean) {
        beans.add(bean);

        /*boolean isAbstract = Modifier.isAbstract(bean.getModifiers());
        EditorsRegistry editorEditorsRegistry = EditorsRegistry.getInstance();

        //if (isAbstract)
         //editorEditorsRegistry.registerEditor(bean, DSLBeanEditor.class);
        /*else
            editorEditorsRegistry.registerEditor(bean, DSLBeanEditor.class);*/
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