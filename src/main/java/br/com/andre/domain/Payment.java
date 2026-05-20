package br.com.andre.domain;

import br.com.andre.domain.enumeration.PaymentMethod;
import br.com.andre.domain.enumeration.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Payment.
 */
@Table("payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("method")
    private PaymentMethod method;

    @NotNull(message = "must not be null")
    @Column("status")
    private PaymentStatus status;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("amount")
    private BigDecimal amount;

    @Size(max = 100)
    @Column("transaction_code")
    private String transactionCode;

    @Column("paid_date")
    private Instant paidDate;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "user", "shippingAddress" }, allowSetters = true)
    private CustomerOrder order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentMethod getMethod() {
        return this.method;
    }

    public Payment method(PaymentMethod method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public Payment status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public String getTransactionCode() {
        return this.transactionCode;
    }

    public Payment transactionCode(String transactionCode) {
        this.setTransactionCode(transactionCode);
        return this;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Instant getPaidDate() {
        return this.paidDate;
    }

    public Payment paidDate(Instant paidDate) {
        this.setPaidDate(paidDate);
        return this;
    }

    public void setPaidDate(Instant paidDate) {
        this.paidDate = paidDate;
    }

    public CustomerOrder getOrder() {
        return this.order;
    }

    public void setOrder(CustomerOrder customerOrder) {
        this.order = customerOrder;
        this.orderId = customerOrder != null ? customerOrder.getId() : null;
    }

    public Payment order(CustomerOrder customerOrder) {
        this.setOrder(customerOrder);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long customerOrder) {
        this.orderId = customerOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", method='" + getMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", transactionCode='" + getTransactionCode() + "'" +
            ", paidDate='" + getPaidDate() + "'" +
            "}";
    }
}
