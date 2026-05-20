package br.com.andre.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.ShoppingCartItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoppingCartItemDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    private Integer quantity;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    private ShoppingCartDTO cart;

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

    public ShoppingCartDTO getCart() {
        return cart;
    }

    public void setCart(ShoppingCartDTO cart) {
        this.cart = cart;
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
        if (!(o instanceof ShoppingCartItemDTO)) {
            return false;
        }

        ShoppingCartItemDTO shoppingCartItemDTO = (ShoppingCartItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shoppingCartItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingCartItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", cart=" + getCart() +
            ", product=" + getProduct() +
            "}";
    }
}
