package br.com.andre.service.dto;

import br.com.andre.domain.enumeration.CustomerOrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.CustomerOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String orderNumber;

    @NotNull(message = "must not be null")
    private Instant orderDate;

    @NotNull(message = "must not be null")
    private CustomerOrderStatus status;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0")
    private BigDecimal discount;

    @DecimalMin(value = "0")
    private BigDecimal shippingCost;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal totalPrice;

    @Size(max = 500)
    private String notes;

    private CustomerDTO customer;

    private UserDTO user;

    private AddressDTO shippingAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public CustomerOrderStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public AddressDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrderDTO)) {
            return false;
        }

        CustomerOrderDTO customerOrderDTO = (CustomerOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderDTO{" +
            "id=" + getId() +
            ", orderNumber='" + getOrderNumber() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", subtotal=" + getSubtotal() +
            ", discount=" + getDiscount() +
            ", shippingCost=" + getShippingCost() +
            ", totalPrice=" + getTotalPrice() +
            ", notes='" + getNotes() + "'" +
            ", customer=" + getCustomer() +
            ", user=" + getUser() +
            ", shippingAddress=" + getShippingAddress() +
            "}";
    }
}
