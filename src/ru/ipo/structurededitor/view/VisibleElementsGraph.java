package ru.ipo.structurededitor.view;

import ru.ipo.structurededitor.view.elements.VisibleElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 14.01.2010
 * Time: 20:05:30
 */
public class VisibleElementsGraph {
    public enum Direction {
        Up, Down, Left, Right,
    }

    private static class Position {
        public int left, right, top, bottom;

        private Position(int left, int right, int top, int bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

    private HashMap<VisibleElement, Position> e2pos = new HashMap<VisibleElement, Position>();
    private int lastLine = -1;
    private int[] lastColumn = new int[0];

    public VisibleElementsGraph(VisibleElement root) {
        dfs(0, 0, root);
    }

    private void dfs(int line, int col, VisibleElement element) {
        if (element.getChildrenCount() == 0) {
            final int dline = line + element.getHeight() - 1;
            final int lcol = col + element.getWidth();
            e2pos.put(element, new Position(col, lcol, line, dline));

            //update last column
            if (dline > lastLine) {
                lastLine = dline;
                int[] newLastColumn = new int[lastLine + 1];
                System.arraycopy(lastColumn, 0, newLastColumn, 0, lastColumn.length);
                lastColumn = newLastColumn;
            }

            for (int l = line; l <= dline; l++) {
                if (lcol > lastColumn[l])
                    lastColumn[l] = lcol;
            }

        } else {
            for (int i = 0; i < element.getChildrenCount(); i++) {
                final TextPosition tp = element.getChildPosition(i);
                dfs(line + tp.getLine(), col + tp.getColumn(), element.getChild(i));
            }
        }
    }

    public TextPosition normalize(TextPosition p, Direction dir) {
        int x0 = p.getColumn(), y0 = p.getLine();
        if (x0 < 0 && y0 == 0) {
            return new TextPosition(0, 0);
        }
        if (x0 > lastColumn[lastLine] && y0 == lastLine) {
            return new TextPosition(y0, lastColumn[lastLine]);
        }
        if (y0 < 0)
            y0 = 0;
        else if (y0 > lastLine) y0 = lastLine;

        if (x0 < 0) y0--;
        else if (x0 > lastColumn[y0]) {
            if (dir == VisibleElementsGraph.Direction.Down || dir == VisibleElementsGraph.Direction.Up)
                x0 = lastColumn[y0];
            else {

                y0++;
                x0 = 0;
            }
        }

        if (y0 < 0) y0 = 0;
        else if (y0 > lastLine) y0 = lastLine;

        if (x0 < 0) x0 = lastColumn[y0];
        else if (x0 > lastColumn[y0])
            x0 = 0;

        return new TextPosition(y0, x0);
    }

    public VisibleElement getNeighbour(VisibleElement element, Direction dir) {
        int dx, x0, y0;
        final Position p = e2pos.get(element);
        switch (dir) {
            case Up:
                dx = -1;
                x0 = p.left;
                y0 = p.top - 1;
                break;
            case Down:
                dx = 1;
                x0 = p.left;
                y0 = p.bottom + 1;
                break;
            case Left:
                dx = -1;
                x0 = p.left - 1;
                y0 = p.top;
                break;
            case Right:
                dx = 1;
                x0 = p.right + 1;
                y0 = p.top;
                break;
            default:
                throw new IllegalStateException();
        }

        while (true) {
            //normalize position
            if (y0 < 0) return element;
            else if (y0 > lastLine) return element;

            if (x0 < 0) y0--;
            else if (x0 > lastColumn[y0]) y0++;

            if (y0 < 0) return element;
            else if (y0 > lastLine) return element;

            if (x0 < 0) x0 = lastColumn[y0];
            else if (x0 > lastColumn[y0]) x0 = 0;

            VisibleElement e = findElementByPos(x0, y0);
            if (e != null)
                return e;

            x0 += dx;
        }
    }

    public VisibleElement findElementByPos(int x0, int y0) {
        for (Map.Entry<VisibleElement, Position> e : e2pos.entrySet()) {
            final Position p = e.getValue();
            if (p.left <= x0 && p.right >= x0 && p.top <= y0 && p.bottom >= y0)
                return e.getKey();
        }
        return null;
    }
}
