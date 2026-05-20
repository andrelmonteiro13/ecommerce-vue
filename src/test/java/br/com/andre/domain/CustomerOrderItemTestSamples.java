package br.com.andre.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerOrderItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CustomerOrderItem getCustomerOrderItemSample1() {
        return new CustomerOrderItem().id(1L).quantity(1);
    }

    public static CustomerOrderItem getCustomerOrderItemSample2() {
        return new CustomerOrderItem().id(2L).quantity(2);
    }

    public static CustomerOrderItem getCustomerOrderItemRandomSampleGenerator() {
        return new CustomerOrderItem().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
