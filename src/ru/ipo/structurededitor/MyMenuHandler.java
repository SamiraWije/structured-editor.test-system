package ru.ipo.structurededitor;

import geogebra.main.Application;
import ru.ipo.structurededitor.controller.ModificationVector;
import ru.ipo.structurededitor.model.DSLBean;
import ru.ipo.structurededitor.structureBuilder.StructureBuilder;
import ru.ipo.structurededitor.structureSerializer.NodesRegistry;
import ru.ipo.structurededitor.structureSerializer.StructureSerializer;
import ru.ipo.structurededitor.view.StructuredEditorModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Олег
 * Date: 26.08.2010
 * Time: 16:15:33
 * To change this template use File | Settings | File Templates.
 */
public class MyMenuHandler implements ActionListener, ItemListener {
    JFrame f;
    //XMLViewer xmlV;
    StructuredEditor structuredEditor;
    NodesRegistry nodesRegistry;
    String subSystem;
    //public MyMenuHandler(JFrame f, XMLViewer xmlV, StructuredEditor structuredEditor){

    public MyMenuHandler(JFrame f, StructuredEditor structuredEditor, NodesRegistry nodesRegistry, String subSystem) {
        this.f = f;
        //this.xmlV=xmlV;
        this.structuredEditor = structuredEditor;
        this.nodesRegistry = nodesRegistry;
        this.subSystem = subSystem;
    }

    private void refreshEditor(DSLBean st, ModificationVector modificationVector) {
        StructuredEditorModel model = new StructuredEditorModel(st, modificationVector);
        model.setBeansRegistry(structuredEditor.getModel().getBeansRegistry());
        structuredEditor.getModel().setFocusedElement(null);
        structuredEditor.setModel(model);
        structuredEditor.getUI().redrawEditor();
    }

    public void actionPerformed(ActionEvent ae) {
        String arg = ae.getActionCommand();
        //System.out.println("You selected "+arg);
        if (arg.equals("Открыть . . .")) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Загрузка задачи");
            XMLFilter filter = new XMLFilter();
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(f);
            if (returnVal == JFileChooser.APPROVE_OPTION /*&& dir != null && fl != null*/) {
                String fn = fc.getSelectedFile().getAbsolutePath();
                File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");
                Application app = structuredEditor.getApp();
                if (app != null) {
                    app.getGuiManager().loadFile(file, false);
                }
                System.out.println("You've opened the file: " + fn);
                //xmlV.setFileName(fn);
                StructureBuilder structureBuilder = new StructureBuilder(fn, subSystem, structuredEditor.getApp());

                DSLBean bean = structureBuilder.getStructure();
                refreshEditor(bean, structuredEditor.getModel().getModificationVector());
                structuredEditor.getModel().getModificationVector().clearVector();


                //EmptyFieldsRegistry.getInstance().clear();
            }

        } else if (arg.equals("Сохранить . . .")) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Сохранение задачи");
            XMLFilter filter = new XMLFilter();
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(f);
            if (returnVal == JFileChooser.APPROVE_OPTION /*&& dir != null && fl != null*/) {
                String fn = fc.getSelectedFile().getAbsolutePath();
                System.out.println("You've saved the file: " + fn);

                StructureSerializer structureSerializer = new StructureSerializer(fn, nodesRegistry);

                structureSerializer.saveStructure(structuredEditor.getModel().getObject());
                File file = new File(fn.substring(0, fn.lastIndexOf('.')) + ".ggb");
                Application app = structuredEditor.getApp();
                if (app != null) {
                    boolean success = structuredEditor.getApp().saveGeoGebraFile(file);
                    if (success)
                        structuredEditor.getApp().setCurrentFile(file);
                }
            }
        } else if (arg.equals("Выход")) {
            f.setVisible(false);
            System.exit(0);
        } else if (arg.equals("Отменить")) {
            structuredEditor.getModel().getModificationVector().undo();
            refreshEditor(structuredEditor.getModel().getObject(),
                    structuredEditor.getModel().getModificationVector());
        } else if (arg.equals("Повторить")) {
            structuredEditor.getModel().getModificationVector().redo();
            refreshEditor(structuredEditor.getModel().getObject(),
                    structuredEditor.getModel().getModificationVector());
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        f.repaint();
    }
}
