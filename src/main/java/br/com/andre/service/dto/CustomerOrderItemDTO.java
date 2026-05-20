package br.com.andre.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.CustomerOrderItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderItemDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    private Integer quantity;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal totalPrice;

    private CustomerOrderDTO order;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public CustomerOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CustomerOrderDTO order) {
        this.order = order;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrderItemDTO)) {
            return false;
        }

        CustomerOrderItemDTO customerOrderItemDTO = (CustomerOrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerOrderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", totalPrice=" + getTotalPrice() +
            ", order=" + getOrder() +
            ", product=" + getProduct() +
            "}";
    }
}
