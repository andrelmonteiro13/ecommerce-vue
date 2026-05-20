package br.com.andre.domain;

import static br.com.andre.domain.ProductTestSamples.*;
import static br.com.andre.domain.ShoppingCartItemTestSamples.*;
import static br.com.andre.domain.ShoppingCartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoppingCartItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingCartItem.class);
        ShoppingCartItem shoppingCartItem1 = getShoppingCartItemSample1();
        ShoppingCartItem shoppingCartItem2 = new ShoppingCartItem();
        assertThat(shoppingCartItem1).isNotEqualTo(shoppingCartItem2);

        shoppingCartItem2.setId(shoppingCartItem1.getId());
        assertThat(shoppingCartItem1).isEqualTo(shoppingCartItem2);

        shoppingCartItem2 = getShoppingCartItemSample2();
        assertThat(shoppingCartItem1).isNotEqualTo(shoppingCartItem2);
    }

    @Test
    void cartTest() {
        ShoppingCartItem shoppingCartItem = getShoppingCartItemRandomSampleGenerator();
        ShoppingCart shoppingCartBack = getShoppingCartRandomSampleGenerator();

        shoppingCartItem.setCart(shoppingCartBack);
        assertThat(shoppingCartItem.getCart()).isEqualTo(shoppingCartBack);

        shoppingCartItem.cart(null);
        assertThat(shoppingCartItem.getCart()).isNull();
    }

    @Test
    void productTest() {
        ShoppingCartItem shoppingCartItem = getShoppingCartItemRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        shoppingCartItem.setProduct(productBack);
        assertThat(shoppingCartItem.getProduct()).isEqualTo(productBack);

        shoppingCartItem.product(null);
        assertThat(shoppingCartItem.getProduct()).isNull();
    }
}
