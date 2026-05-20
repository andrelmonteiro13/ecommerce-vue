package br.com.andre.service.dto;

import br.com.andre.domain.enumeration.PaymentMethod;
import br.com.andre.domain.enumeration.PaymentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private PaymentMethod method;

    @NotNull(message = "must not be null")
    private PaymentStatus status;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @Size(max = 100)
    private String transactionCode;

    private Instant paidDate;

    private CustomerOrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Instant getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Instant paidDate) {
        this.paidDate = paidDate;
    }

    public CustomerOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CustomerOrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
            "id=" + getId() +
            ", method='" + getMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", transactionCode='" + getTransactionCode() + "'" +
            ", paidDate='" + getPaidDate() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
