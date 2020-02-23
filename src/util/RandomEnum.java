package util;

import java.util.Random;

/**
 * @author BuyWatermelon
 */
public class RandomEnum<E extends Enum<E>> {

    private static final Random RND = new Random();
    private final E[] values;

    public RandomEnum(Class<E> token) {
        values = token.getEnumConstants();
    }

    public E random() {
        return values[RND.nextInt(values.length)];
    }
}
