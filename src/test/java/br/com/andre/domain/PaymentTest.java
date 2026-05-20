package br.com.andre.domain;

import static br.com.andre.domain.CustomerOrderTestSamples.*;
import static br.com.andre.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.andre.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void orderTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        CustomerOrder customerOrderBack = getCustomerOrderRandomSampleGenerator();

        payment.setOrder(customerOrderBack);
        assertThat(payment.getOrder()).isEqualTo(customerOrderBack);

        payment.order(null);
        assertThat(payment.getOrder()).isNull();
    }
}
