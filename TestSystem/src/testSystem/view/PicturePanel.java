/* Вывод картинок в условии задачи (в частности, для ЦОС)*/
package testSystem.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: olegperch
 * Date: 14.05.12
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class PicturePanel extends JPanel {
    private static final Logger log = Logger.getLogger(PicturePanel.class.getName());

    private Image img = null;

    public PicturePanel(){

    }
    public PicturePanel(String file) {
        loadImage(file);
    }

    public void loadImage(String file){
        // create the image using the toolkit
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error in image loading from file: " + file, e);
        }

        Dimension size = getSize();
        size.width=((BufferedImage)img).getWidth();
        setSize(size);
        repaint();
    }
    public void paint(Graphics g) {
        super.paint(g);
        // the size of the component
        Dimension d = getSize();
        // the internal margins of the component
        Insets i = getInsets();
        // draw to fill the entire component
        //g.drawImage(img, i.left, i.top, d.width - i.left - i.right, d.height - i.top - i.bottom, this);
       if (g.drawImage(img,0,0,null)){
           log.fine("Image OK");
       } else {
           log.warning("Bad Image!");
       }
    }

}
