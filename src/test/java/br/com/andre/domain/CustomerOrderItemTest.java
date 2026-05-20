package br.com.andre.domain;

import static br.com.andre.domain.CustomerOrderItemTestSamples.*;
import static br.com.andre.domain.CustomerOrderTestSamples.*;
import static br.com.andre.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerOrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerOrderItem.class);
        CustomerOrderItem customerOrderItem1 = getCustomerOrderItemSample1();
        CustomerOrderItem customerOrderItem2 = new CustomerOrderItem();
        assertThat(customerOrderItem1).isNotEqualTo(customerOrderItem2);

        customerOrderItem2.setId(customerOrderItem1.getId());
        assertThat(customerOrderItem1).isEqualTo(customerOrderItem2);

        customerOrderItem2 = getCustomerOrderItemSample2();
        assertThat(customerOrderItem1).isNotEqualTo(customerOrderItem2);
    }

    @Test
    void orderTest() {
        CustomerOrderItem customerOrderItem = getCustomerOrderItemRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        customerOrderItem.setOrder(customerOrderBack);
        assertThat(customerOrderItem.getOrder()).isEqualTo(customerOrderBack);

        customerOrderItem.order(null);
        assertThat(customerOrderItem.getOrder()).isNull();
    }

    @Test
    void productTest() {
        CustomerOrderItem customerOrderItem = getCustomerOrderItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        customerOrderItem.setProduct(productBack);
        assertThat(customerOrderItem.getProduct()).isEqualTo(productBack);

        customerOrderItem.product(null);
        assertThat(customerOrderItem.getProduct()).isNull();
    }
}
