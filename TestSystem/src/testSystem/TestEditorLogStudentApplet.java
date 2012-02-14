package testSystem;

import javax.swing.*;
import java.applet.Applet;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 16.12.11
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class TestEditorLogStudentApplet extends JApplet {
    @Override
    public void init(){
      //String taskPath=System.getenv("TASKPATH");
      //  String curDir = System.getProperty("user.dir");
      new TestEditorLogStudent(this,this.getParameter("task"));
    }

}
