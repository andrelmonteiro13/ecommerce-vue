package br.com.andre.domain;

import br.com.andre.domain.enumeration.CustomerOrderStatus;
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
 * A CustomerOrder.
 */
@Table("customer_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("order_number")
    private String orderNumber;

    @NotNull(message = "must not be null")
    @Column("order_date")
    private Instant orderDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private CustomerOrderStatus status;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("subtotal")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    @Column("discount")
    private BigDecimal discount;

    @DecimalMin(value = "0")
    @Column("shipping_cost")
    private BigDecimal shippingCost;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("total_price")
    private BigDecimal totalPrice;

    @Size(max = 500)
    @Column("notes")
    private String notes;

    @org.springframework.data.annotation.Transient
    private Customer customer;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Address shippingAddress;

    @Column("customer_id")
    private Long customerId;

    @Column("user_id")
    private Long userId;

    @Column("shipping_address_id")
    private Long shippingAddressId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public CustomerOrder orderNumber(String orderNumber) {
        this.setOrderNumber(orderNumber);
        return this;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Instant getOrderDate() {
        return this.orderDate;
    }

    public CustomerOrder orderDate(Instant orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public CustomerOrderStatus getStatus() {
        return this.status;
    }

    public CustomerOrder status(CustomerOrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public CustomerOrder subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal != null ? subtotal.stripTrailingZeros() : null;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public CustomerOrder discount(BigDecimal discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount != null ? discount.stripTrailingZeros() : null;
    }

    public BigDecimal getShippingCost() {
        return this.shippingCost;
    }

    public CustomerOrder shippingCost(BigDecimal shippingCost) {
        this.setShippingCost(shippingCost);
        return this;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost != null ? shippingCost.stripTrailingZeros() : null;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public CustomerOrder totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public String getNotes() {
        return this.notes;
    }

    public CustomerOrder notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public CustomerOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public CustomerOrder user(User user) {
        this.setUser(user);
        return this;
    }

    public Address getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(Address address) {
        this.shippingAddress = address;
        this.shippingAddressId = address != null ? address.getId() : null;
    }

    public CustomerOrder shippingAddress(Address address) {
        this.setShippingAddress(address);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getShippingAddressId() {
        return this.shippingAddressId;
    }

    public void setShippingAddressId(Long address) {
        this.shippingAddressId = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrder{" +
            "id=" + getId() +
            ", orderNumber='" + getOrderNumber() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", subtotal=" + getSubtotal() +
            ", discount=" + getDiscount() +
            ", shippingCost=" + getShippingCost() +
            ", totalPrice=" + getTotalPrice() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
