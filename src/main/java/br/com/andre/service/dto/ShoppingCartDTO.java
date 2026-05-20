package br.com.andre.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.ShoppingCart} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoppingCartDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant createdDate;

    private Instant updatedDate;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingCartDTO)) {
            return false;
        }

        ShoppingCartDTO shoppingCartDTO = (ShoppingCartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shoppingCartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingCartDTO{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
