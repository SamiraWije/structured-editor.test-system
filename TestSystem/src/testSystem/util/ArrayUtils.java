package testSystem.util;

/**
 * User: Vladislav Dolbilov (darl@yandex-team.ru)
 */
public class ArrayUtils {

    /**
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     *
     * @param oldArray the old array, to be reallocated.
     * @param newSize  the new array size.
     * @return A new array with the same contents.
     */
    @SuppressWarnings({"SuspiciousSystemArraycopy"})
    public static Object resizeArray(Object oldArray, int newSize) {   //todo remove all usage of this method
        try {
            int oldSize = java.lang.reflect.Array.getLength(oldArray);
            Class elementType = oldArray.getClass().getComponentType();
            Object newArray = java.lang.reflect.Array.newInstance(
                    elementType, newSize);
            int preserveLength = Math.min(oldSize, newSize);
            if (preserveLength > 0) {
                System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
            }
            return newArray;
        } catch (Exception e) {
            System.out.println("Error in array resizing! " + e);
        }
        return null;
    }
}
