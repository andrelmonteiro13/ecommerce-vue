package br.com.andre.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CustomerOrderItem.
 */
@Table("customer_order_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    @Column("quantity")
    private Integer quantity;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("unit_price")
    private BigDecimal unitPrice;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("total_price")
    private BigDecimal totalPrice;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "user", "shippingAddress" }, allowSetters = true)
    private CustomerOrder order;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Product product;

    @Column("order_id")
    private Long orderId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerOrderItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public CustomerOrderItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public CustomerOrderItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice.stripTrailingZeros() : null;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public CustomerOrderItem totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public CustomerOrder getOrder() {
        return this.order;
    }

    public void setOrder(CustomerOrder customerOrder) {
        this.order = customerOrder;
        this.orderId = customerOrder != null ? customerOrder.getId() : null;
    }

    public CustomerOrderItem order(CustomerOrder customerOrder) {
        this.setOrder(customerOrder);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public CustomerOrderItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long customerOrder) {
        this.orderId = customerOrder;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrderItem)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerOrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
