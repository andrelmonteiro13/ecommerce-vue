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
 * A ShoppingCartItem.
 */
@Table("shopping_cart_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoppingCartItem implements Serializable {

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

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private ShoppingCart cart;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Product product;

    @Column("cart_id")
    private Long cartId;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoppingCartItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ShoppingCartItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public ShoppingCartItem unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice.stripTrailingZeros() : null;
    }

    public ShoppingCart getCart() {
        return this.cart;
    }

    public void setCart(ShoppingCart shoppingCart) {
        this.cart = shoppingCart;
        this.cartId = shoppingCart != null ? shoppingCart.getId() : null;
    }

    public ShoppingCartItem cart(ShoppingCart shoppingCart) {
        this.setCart(shoppingCart);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ShoppingCartItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getCartId() {
        return this.cartId;
    }

    public void setCartId(Long shoppingCart) {
        this.cartId = shoppingCart;
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
        if (!(o instanceof ShoppingCartItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ShoppingCartItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingCartItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
