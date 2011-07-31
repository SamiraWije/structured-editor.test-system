package testSystem.lang.comb;

/**
 * Created by IntelliJ IDEA.
 * User: Ilya
 * Date: 05.08.2010
 * Time: 16:16:05
 */
public abstract class CalculableExpr extends Expr {
    String ce;

    public String getCe() {
        return ce;
    }

    public void setCe(String ce) {
        this.ce = ce;
    }
}
