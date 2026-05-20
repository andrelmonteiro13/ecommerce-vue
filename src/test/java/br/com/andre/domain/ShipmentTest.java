package br.com.andre.domain;

import static br.com.andre.domain.CustomerOrderTestSamples.*;
import static br.com.andre.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipment.class);
        Shipment shipment1 = getShipmentSample1();
        Shipment shipment2 = new Shipment();
        assertThat(shipment1).isNotEqualTo(shipment2);

        shipment2.setId(shipment1.getId());
        assertThat(shipment1).isEqualTo(shipment2);

        shipment2 = getShipmentSample2();
        assertThat(shipment1).isNotEqualTo(shipment2);
    }

    @Test
    void orderTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        shipment.setOrder(customerOrderBack);
        assertThat(shipment.getOrder()).isEqualTo(customerOrderBack);

        shipment.order(null);
        assertThat(shipment.getOrder()).isNull();
    }
}
