package util;

public class General {
    public static <T> int arrayHash(T[] array) {
        int hash = 0;

        for (int i = 0; i < array.length; i++) {
            hash += Math.pow(array[i].hashCode(), i + 1);
        }

        return hash;
    }
}
