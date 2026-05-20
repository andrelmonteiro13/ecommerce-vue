package br.com.andre.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerOrderItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerOrderItemDTO.class);
        CustomerOrderItemDTO customerOrderItemDTO1 = new CustomerOrderItemDTO();
        customerOrderItemDTO1.setId(1L);
        CustomerOrderItemDTO customerOrderItemDTO2 = new CustomerOrderItemDTO();
        assertThat(customerOrderItemDTO1).isNotEqualTo(customerOrderItemDTO2);
        customerOrderItemDTO2.setId(customerOrderItemDTO1.getId());
        assertThat(customerOrderItemDTO1).isEqualTo(customerOrderItemDTO2);
        customerOrderItemDTO2.setId(2L);
        assertThat(customerOrderItemDTO1).isNotEqualTo(customerOrderItemDTO2);
        customerOrderItemDTO1.setId(null);
        assertThat(customerOrderItemDTO1).isNotEqualTo(customerOrderItemDTO2);
    }
}
