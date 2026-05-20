package br.com.andre.service.mapper;

import static br.com.andre.domain.CustomerOrderItemAsserts.*;
import static br.com.andre.domain.CustomerOrderItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerOrderItemMapperTest {

    private CustomerOrderItemMapper customerOrderItemMapper;

    @BeforeEach
    void setUp() {
        customerOrderItemMapper = new CustomerOrderItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCustomerOrderItemSample1();
        var actual = customerOrderItemMapper.toEntity(customerOrderItemMapper.toDto(expected));
        assertCustomerOrderItemAllPropertiesEquals(expected, actual);
    }
}
