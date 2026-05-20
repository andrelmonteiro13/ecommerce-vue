package br.com.andre.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ShoppingCartItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ShoppingCartItem getShoppingCartItemSample1() {
        return new ShoppingCartItem().id(1L).quantity(1);
    }

    public static ShoppingCartItem getShoppingCartItemSample2() {
        return new ShoppingCartItem().id(2L).quantity(2);
    }

    public static ShoppingCartItem getShoppingCartItemRandomSampleGenerator() {
        return new ShoppingCartItem().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
