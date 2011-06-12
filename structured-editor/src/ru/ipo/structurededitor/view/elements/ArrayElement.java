package ru.ipo.structurededitor.view.elements;

import ru.ipo.structurededitor.view.StructuredEditorModel;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 28.07.2010
 * Time: 12:21:18
 * To change this template use File | Settings | File Templates.
 */
public class ArrayElement extends CompositeElement {
    private char spaceChar;

    public ArrayElement(StructuredEditorModel model, Orientation orientation, char spaceChar) {
        super(model, orientation, spaceChar);

    }

    public char getSpaceChar() {
        return spaceChar;
    }

    /*@Override
    public void Refresh(RefreshProperties rp){

     super.Refresh(rp);
   }  */
    /*@Override
   public void processKeyEvent(KeyEvent e) {
    switch (e.getKeyCode()) {
           case KeyEvent.VK_DELETE:
               buttonDelete();
               break;
           case KeyEvent.VK_BACK_SPACE:
               buttonBackSpace();
               break;
           case KeyEvent.VK_INSERT:
               buttonInsert();
               break;
           case KeyEvent.VK_ENTER:
               buttonEnter();
               e.consume();
               break;

         }
   }

   private void buttonEnter() {
       //To change body of created methods use File | Settings | File Templates.
       add()
   } */
}
