package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Yura
 * Date: 16.03.2010
 * Time: 0:04:13
 * To change this template use File | Settings | File Templates.
 */
public class NextArray2Element extends TextEditorElement {

    private Class<? extends DSLBean> bean = null;

    public NextArray2Element(Class<? extends DSLBean> bean, StructuredEditorModel model) {
        super(model);
        this.setBean(bean);
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                setText(this.getBean().getSimpleName());
                e.consume();
                break;
        }

        super.processKeyEvent(e);
    }

    public Class<? extends DSLBean> getBean() {
        return bean;
    }

    public void setBean(Class<? extends DSLBean> bean) {
        this.bean = bean;
    }
}
