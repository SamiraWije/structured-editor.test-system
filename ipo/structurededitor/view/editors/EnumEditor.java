package ru.ipo.structurededitor.view.editors;

import ru.ipo.structurededitor.controller.EmptyFieldsRegistry;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.model.DSLBeansRegistry;
import ru.ipo.structurededitor.view.EditorRenderer;
import ru.ipo.structurededitor.view.StructuredEditorModel;
import ru.ipo.structurededitor.view.elements.ComboBoxTextEditorElement;
import ru.ipo.structurededitor.view.elements.ContainerElement;
import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 03.01.2010
 * Time: 23:49:11
 */
public class EnumEditor extends FieldEditor {

    private ComboBoxTextEditorElement<Enum<?>> EnumSelectionElement;
    //private ContainerElement container;

    public EnumEditor(Object o, String fieldName) {
        super(o, fieldName);
    }

    public EnumEditor(Object o, String fieldName, int index) {
        super(o, fieldName, index);
    }

    @Override
    public VisibleElement createElement(StructuredEditorModel model) {
        EnumSelectionElement = createEnumSelectionElement(model);
        //container = new ContainerElement(model, EnumSelectionElement);
        Field[] possibleValues = new Field[20];
        PropertyDescriptor pd;
        Class<? extends Enum> eclass;
        try {
            pd = new PropertyDescriptor(getFieldName(), getObject().getClass());
            if (isArrItem()) {
                eclass = (Class<? extends Enum>) (pd.getPropertyType().getComponentType());

            } else {
                eclass = (Class<? extends Enum>) (pd.getPropertyType());
            }
            possibleValues = eclass.getFields();
        }
        catch (Exception e) {
            throw new Error("Fail in EnumEditor.createElement()");
        }
        //Method[] possibleMethods = getObject().getClass().getMethods();
        String const_name;
        for (Field pv : possibleValues) {
            if (pv.isEnumConstant()) {
                const_name = pv.getName();
                EnumSelectionElement.addValue(const_name, Enum.valueOf(eclass, const_name));
            }
        }
        // else {
        //       System.out.println("This is not enum ");
        // }
        /*
        container.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                //if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getKeyChar() == '\u0001') //Ctrl+a
                //    container.setSubElement(beanClassSelectionElement);
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        */
        //return container;

        updateElement();

        return EnumSelectionElement;
    }

    @Override
    protected void updateElement() {
        if (EmptyFieldsRegistry.getInstance().isEmpty((DSLBean) getObject(), getFieldName())){
            EnumSelectionElement.setValue(null);
        }
        else {
            EnumSelectionElement.setValue((Enum<?>) getValue());
        }
    }

    private ComboBoxTextEditorElement<Enum<?>> createEnumSelectionElement(final StructuredEditorModel model) {
        final ComboBoxTextEditorElement<Enum<?>> res = new ComboBoxTextEditorElement<Enum<?>>(model);
        res.setEmptyString("[Выберите вариант]");
        res.addPropertyChangeListener("text", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Enum<?> value = res.getValue();
                //System.out.println("enum value =" + value);
                //if (value != null) {
                    setValue(value);
                //}

            }
        });
        /*res.addPropertyChangeListener("refresh", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                VisibleElement.RefreshProperties rp = (VisibleElement.RefreshProperties) evt.getNewValue();
                setEmpty(rp.getEmpty());
                setObject(rp.getObject());
                Enum<?> val = (Enum<?>) getValue();
                if (val == null)
                    res.setText("");
                else
                    res.setValue(val);
                res.resetPosition();
                res.resumeRefresh();

            }
        });*/
        return res;
    }

}