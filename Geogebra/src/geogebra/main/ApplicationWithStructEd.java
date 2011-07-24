package geogebra.main;


import javax.swing.*;

public class ApplicationWithStructEd extends Application {

    public ApplicationWithStructEd(String[] args, JFrame frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public ApplicationWithStructEd(String[] args, AppletImplementation applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }

}
