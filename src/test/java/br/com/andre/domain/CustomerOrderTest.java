package br.com.andre.domain;

import static br.com.andre.domain.AddressTestSamples.*;
import static br.com.andre.domain.CustomerOrderTestSamples.*;
import static br.com.andre.domain.CustomerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerOrder.class);
        CustomerOrder customerOrder1 = getCustomerOrderSample1();
        CustomerOrder customerOrder2 = new CustomerOrder();
        assertThat(customerOrder1).isNotEqualTo(customerOrder2);

        customerOrder2.setId(customerOrder1.getId());
        assertThat(customerOrder1).isEqualTo(customerOrder2);

        customerOrder2 = getCustomerOrderSample2();
        assertThat(customerOrder1).isNotEqualTo(customerOrder2);
    }

    @Test
    void customerTest() {
        CustomerOrder customerOrder = getCustomerOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        customerOrder.setCustomer(customerBack);
        assertThat(customerOrder.getCustomer()).isEqualTo(customerBack);

        customerOrder.customer(null);
        assertThat(customerOrder.getCustomer()).isNull();
    }

    @Test
    void shippingAddressTest() {
        CustomerOrder customerOrder = getCustomerOrderRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        customerOrder.setShippingAddress(addressBack);
        assertThat(customerOrder.getShippingAddress()).isEqualTo(addressBack);

        customerOrder.shippingAddress(null);
        assertThat(customerOrder.getShippingAddress()).isNull();
    }
}
