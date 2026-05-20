package br.com.andre.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShoppingCartItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingCartItemDTO.class);
        ShoppingCartItemDTO shoppingCartItemDTO1 = new ShoppingCartItemDTO();
        shoppingCartItemDTO1.setId(1L);
        ShoppingCartItemDTO shoppingCartItemDTO2 = new ShoppingCartItemDTO();
        assertThat(shoppingCartItemDTO1).isNotEqualTo(shoppingCartItemDTO2);
        shoppingCartItemDTO2.setId(shoppingCartItemDTO1.getId());
        assertThat(shoppingCartItemDTO1).isEqualTo(shoppingCartItemDTO2);
        shoppingCartItemDTO2.setId(2L);
        assertThat(shoppingCartItemDTO1).isNotEqualTo(shoppingCartItemDTO2);
        shoppingCartItemDTO1.setId(null);
        assertThat(shoppingCartItemDTO1).isNotEqualTo(shoppingCartItemDTO2);
    }
}
