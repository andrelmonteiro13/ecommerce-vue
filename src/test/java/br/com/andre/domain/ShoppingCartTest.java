package br.com.andre.domain;

import static br.com.andre.domain.CustomerTestSamples.*;
import static br.com.andre.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingCart.class);
        ShoppingCart shoppingCart1 = getShoppingCartSample1();
        ShoppingCart shoppingCart2 = new ShoppingCart();
        assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);

        shoppingCart2.setId(shoppingCart1.getId());
        assertThat(shoppingCart1).isEqualTo(shoppingCart2);

        shoppingCart2 = getShoppingCartSample2();
        assertThat(shoppingCart1).isNotEqualTo(shoppingCart2);
    }

    @Test
    void customerTest() {
        ShoppingCart shoppingCart = getShoppingCartRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        shoppingCart.setCustomer(customerBack);
        assertThat(shoppingCart.getCustomer()).isEqualTo(customerBack);

        shoppingCart.customer(null);
        assertThat(shoppingCart.getCustomer()).isNull();
    }
}
