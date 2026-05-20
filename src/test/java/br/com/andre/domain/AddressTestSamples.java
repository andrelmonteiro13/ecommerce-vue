package br.com.andre.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address()
            .id(1L)
            .street("street1")
            .number("number1")
            .complement("complement1")
            .district("district1")
            .city("city1")
            .state("state1")
            .zipCode("zipCode1")
            .country("country1");
    }

    public static Address getAddressSample2() {
        return new Address()
            .id(2L)
            .street("street2")
            .number("number2")
            .complement("complement2")
            .district("district2")
            .city("city2")
            .state("state2")
            .zipCode("zipCode2")
            .country("country2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(longCount.incrementAndGet())
            .street(UUID.randomUUID().toString())
            .number(UUID.randomUUID().toString())
            .complement(UUID.randomUUID().toString())
            .district(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .zipCode(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString());
    }
}
