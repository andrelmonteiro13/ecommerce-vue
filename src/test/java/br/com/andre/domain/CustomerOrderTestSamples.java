package br.com.andre.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CustomerOrder getCustomerOrderSample1() {
        return new CustomerOrder().id(1L).orderNumber("orderNumber1").notes("notes1");
    }

    public static CustomerOrder getCustomerOrderSample2() {
        return new CustomerOrder().id(2L).orderNumber("orderNumber2").notes("notes2");
    }

    public static CustomerOrder getCustomerOrderRandomSampleGenerator() {
        return new CustomerOrder()
            .id(longCount.incrementAndGet())
            .orderNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
