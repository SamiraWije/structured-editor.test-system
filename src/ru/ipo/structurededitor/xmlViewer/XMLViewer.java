package ru.ipo.structurededitor.xmlViewer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ru.ipo.structurededitor.view.StructuredEditorUI;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 28.08.2010
 * Time: 12:24:05
 * To change this template use File | Settings | File Templates.
 */
public class XMLViewer extends JComponent implements Scrollable {
    private String fileName;
    private int x, y, x_max;
    private Graphics gr;
    private int charHeight, charWidth, charDescent, charAscent;


    private final Color ELEMENT_NODE_COLOR = Color.BLUE, ATTR_VALUE_COLOR = Color.GREEN,
            TEXT_NODE_COLOR = Color.BLACK;
    private final int X_MIN = 0, Y_MIN = 15;

    public XMLViewer(String fileName) {
        this.fileName = fileName;
    }


    // implementing Scrollable

    public Dimension getUIPreferredSize() {
        return new Dimension(x_max, y);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension parentDimension = getParent().getSize();
        parentDimension = parentDimension == null
                ? new Dimension(1, 1)
                : parentDimension;

        Dimension uiDimension = getUIPreferredSize();
        uiDimension = uiDimension == null ? new Dimension(1, 1) : uiDimension;

        if (uiDimension.width > parentDimension.width)
            parentDimension.width = uiDimension.width;
        if (uiDimension.height > parentDimension.height)
            parentDimension.height = uiDimension.height;

        return parentDimension;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getUIPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        return getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


    public boolean getScrollableTracksViewportWidth() {
        return false;
    }


    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
                                          int direction) {
        switch (orientation) {
            case SwingConstants.VERTICAL:
                return charHeight;
            case SwingConstants.HORIZONTAL:
                return charWidth;
        }
        return 0; // may not occur
    }
    // end of implementing

    private void drawStr(Color color, String str) {
        gr.setColor(color);
        gr.drawString(str, x, y);
        x += str.length() * charWidth;
        if (x > x_max)
            x_max = x;
    }

    private void drawStrLn(Color color, String str) {
        gr.setColor(color);
        gr.drawString(str, x, y);
        x += str.length() * charWidth;
        if (x > x_max)
            x_max = x;
        x = X_MIN;
        y += charHeight;
    }

    private void TraverseDOM() {
        try {
            Document document;
            // obtain the default parser
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // set error handler for validation errors
            builder.setErrorHandler(new MyErrorHandler());

            // obtain document object from XML document
            document = builder.parse(new File(fileName));
            x = X_MIN;
            y = Y_MIN;
            x_max = X_MIN;

            processNode(document);
        } catch (SAXParseException spe) {
            System.err.println(
                    "Parse error: " + spe.getMessage());
            System.exit(1);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (FileNotFoundException fne) {
            System.err.println("File \'"
                    + fileName + "\' not found. ");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processNode(Node currentNode) {
        switch (currentNode.getNodeType()) {

            // process a Document node
            case Node.DOCUMENT_NODE:
                Document doc = (Document) currentNode;

                System.out.println(
                        "Document node: " + doc.getNodeName() +
                                "\nRoot element: " +
                                doc.getDocumentElement().getNodeName());
                processChildNodes(doc.getChildNodes(), false);
                break;

            // process an Element node*/
            case Node.ELEMENT_NODE:

                drawStr(ELEMENT_NODE_COLOR, "<" + currentNode.getNodeName());
                System.out.println("\nElement node: " +
                        currentNode.getNodeName());
                NamedNodeMap attributeNodes =
                        currentNode.getAttributes();

                for (int i = 0; i < attributeNodes.getLength(); i++) {
                    Attr attribute = (Attr) attributeNodes.item(i);
                    drawStr(ELEMENT_NODE_COLOR, " " + attribute.getNodeName() + " = ");
                    drawStr(ATTR_VALUE_COLOR, "\"" + attribute.getNodeValue() + "\"");
                    System.out.println("\tAttribute: " +
                            attribute.getNodeName() + " ; Value = " +
                            attribute.getNodeValue());

                }
                if (processChildNodes(currentNode.getChildNodes(), true)) {
                    drawStrLn(ELEMENT_NODE_COLOR, "</" + currentNode.getNodeName() + ">");
                }

                break;

            // process a text node and a CDATA section
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                Text text = (Text) currentNode;

                if (!text.getNodeValue().trim().equals("")) {
                    drawStrLn(TEXT_NODE_COLOR, text.getNodeValue());
                    System.out.println("\tText: " +
                            text.getNodeValue());
                }
                break;
        }
    }

    private boolean processChildNodes(NodeList children, boolean innerNode) {
        if (children.getLength() != 0) {
            if (innerNode) drawStrLn(ELEMENT_NODE_COLOR, ">");

            for (int i = 0; i < children.getLength(); i++)
                processNode(children.item(i));
            return true;
        } else {
            if (innerNode) drawStrLn(ELEMENT_NODE_COLOR, "/>");
            return false;
        }
    }


    @Override
    public void paint(Graphics gr) {
        gr.setColor(Color.BLACK);
        Font f = StructuredEditorUI.FONT;
        gr.setFont(f);
        this.gr = gr;
        FontMetrics fontMetrics = gr.getFontMetrics();
        charHeight = fontMetrics.getHeight();
        charWidth = fontMetrics.charWidth('m');
        charDescent = fontMetrics.getDescent();
        charAscent = fontMetrics.getAscent();
        TraverseDOM();

        //gr.drawString("File: "+fileName, 5, 50);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        repaint();
    }
}
