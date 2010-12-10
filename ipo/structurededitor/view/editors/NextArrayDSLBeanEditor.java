package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.testLang.Bean1;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */

public class NextArrayDSLBeanEditor extends FieldEditor {

    private NextArray2Element nextArrayBeanElement;
    private ContainerElement container;

    public NextArrayDSLBeanEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        nextArrayBeanElement = createNextArrayBeanElement(model);
        container = new ContainerElement(model, nextArrayBeanElement);

        return container;
    }

    @Override
    protected void updateElement() {
        //TODO implement
    }

    private NextArray2Element createNextArrayBeanElement(final StructuredEditorModel model) {
        Class<? extends DSLBean> bean = DSLBeansRegistry.getInstance().getBeanByName(this.getFieldName());
        final NextArray2Element res = new NextArray2Element(bean, model);

        res.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Class<? extends DSLBean> value = res.getBean();
                if (value != null)
                    setNewBean(value, model);
            }
        });

        return res;
    }

    private void setNewBean(Class<? extends DSLBean> beanClass, StructuredEditorModel model) {
        try {
            DSLBean bean = beanClass.newInstance();
            EditorRenderer renderer = new EditorRenderer(model, bean);
            container.setSubElement(renderer.getRenderResult());
        } catch (Exception e) {
            throw new Error("Failed to initialize DSL Bean");
        }
    }

}