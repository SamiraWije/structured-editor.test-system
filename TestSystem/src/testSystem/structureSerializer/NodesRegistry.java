package testSystem.structureSerializer;

import org.w3c.dom.Node;
import ru.ipo.structurededitor.model.DSLBean;

import java.util.HashMap;

/**
 * Сопоставление Beans и XML-узлов
 */
public class NodesRegistry {

    /*private static NodesRegistry instance = new NodesRegistry();

    public static NodesRegistry getInstance() {
        return instance;
    } */

    /**
     * Узел по умолчанию для всех тех полей, для которых не нашлось ничего получше
     */
    private Node defaultNode;

    /**
     * Узел для пустых элементов
     */

    private Node emptyNode;


    /**
     * Соответствие конкретных свойств и узлов
     */
    private HashMap<String, Node> propToNode = new HashMap<String, Node>();

    /**
     * Соответствие типов свойств и узлов
     */
    private HashMap<Class<?>, Node> propTypeToNode = new HashMap<Class<?>, Node>();

    public Node getDefaultNode() {
        return defaultNode.cloneNode(true);
    }

    public void setDefaultNode(Node defaultNode) {
        this.defaultNode = defaultNode;
    }

    public Node getEmptyNode() {
        return emptyNode.cloneNode(true);
    }

    public void setEmptyNode(Node emptyNode) {
        this.emptyNode = emptyNode;
    }

    /**
     * Задаем конкретный узел для поля DSLBean
     *
     * @param beanClass    класс
     * @param propertyName имя свойства
     * @param node         узел
     */
    public void registerNode(Class<? extends DSLBean> beanClass, String propertyName, Node node) {
        String key = getKey(beanClass, propertyName);
        propToNode.put(key, node);
    }

    public void registerNode(Class<?> cls, Node node) {
        propTypeToNode.put(cls, node);
    }

    private String getKey(Class<?> beanClass, String propertyName) {
        return beanClass.getName() + "." + propertyName;
    }


    /**
     * Получение узла для поля DSLBean
     *
     * @param beanClass    класс бина
     * @param propertyName имя свойства
     * @return узел для свойства
     */
    public Node getNode(Class<?> beanClass, String propertyName) {
        try {
            Node pec = propToNode.get(getKey(beanClass, propertyName));
            if (pec != null)
                return pec.cloneNode(true);
            return defaultNode;
        } catch (Exception e) {
            throw new Error("Failed to return node: ", e);
        }
    }

    public Node getNode(Class<?> cls) {
        try {
            Node pec = propTypeToNode.get(cls);
            if (pec != null)
                return pec.cloneNode(true);

            return defaultNode;
        } catch (Exception e) {
            throw new Error("Failed to return node: ", e);
        }
    }

    public NodesRegistry() {

    }
}
