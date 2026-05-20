package br.com.andre.domain.criteria;

import br.com.andre.domain.enumeration.CustomerOrderStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link br.com.andre.domain.CustomerOrder} entity. This class is used
 * in {@link br.com.andre.web.rest.CustomerOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customer-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerOrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CustomerOrderStatus
     */
    public static class CustomerOrderStatusFilter extends Filter<CustomerOrderStatus> {

        public CustomerOrderStatusFilter() {}

        public CustomerOrderStatusFilter(CustomerOrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public CustomerOrderStatusFilter copy() {
            return new CustomerOrderStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orderNumber;

    private InstantFilter orderDate;

    private CustomerOrderStatusFilter status;

    private BigDecimalFilter subtotal;

    private BigDecimalFilter discount;

    private BigDecimalFilter shippingCost;

    private BigDecimalFilter totalPrice;

    private StringFilter notes;

    private LongFilter customerId;

    private LongFilter userId;

    private LongFilter shippingAddressId;

    private Boolean distinct;

    public CustomerOrderCriteria() {}

    public CustomerOrderCriteria(CustomerOrderCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.orderNumber = other.optionalOrderNumber().map(StringFilter::copy).orElse(null);
        this.orderDate = other.optionalOrderDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CustomerOrderStatusFilter::copy).orElse(null);
        this.subtotal = other.optionalSubtotal().map(BigDecimalFilter::copy).orElse(null);
        this.discount = other.optionalDiscount().map(BigDecimalFilter::copy).orElse(null);
        this.shippingCost = other.optionalShippingCost().map(BigDecimalFilter::copy).orElse(null);
        this.totalPrice = other.optionalTotalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.notes = other.optionalNotes().map(StringFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.shippingAddressId = other.optionalShippingAddressId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerOrderCriteria copy() {
        return new CustomerOrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getOrderNumber() {
        return orderNumber;
    }

    public Optional<StringFilter> optionalOrderNumber() {
        return Optional.ofNullable(orderNumber);
    }

    public StringFilter orderNumber() {
        if (orderNumber == null) {
            setOrderNumber(new StringFilter());
        }
        return orderNumber;
    }

    public void setOrderNumber(StringFilter orderNumber) {
        this.orderNumber = orderNumber;
    }

    public InstantFilter getOrderDate() {
        return orderDate;
    }

    public Optional<InstantFilter> optionalOrderDate() {
        return Optional.ofNullable(orderDate);
    }

    public InstantFilter orderDate() {
        if (orderDate == null) {
            setOrderDate(new InstantFilter());
        }
        return orderDate;
    }

    public void setOrderDate(InstantFilter orderDate) {
        this.orderDate = orderDate;
    }

    public CustomerOrderStatusFilter getStatus() {
        return status;
    }

    public Optional<CustomerOrderStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CustomerOrderStatusFilter status() {
        if (status == null) {
            setStatus(new CustomerOrderStatusFilter());
        }
        return status;
    }

    public void setStatus(CustomerOrderStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getSubtotal() {
        return subtotal;
    }

    public Optional<BigDecimalFilter> optionalSubtotal() {
        return Optional.ofNullable(subtotal);
    }

    public BigDecimalFilter subtotal() {
        if (subtotal == null) {
            setSubtotal(new BigDecimalFilter());
        }
        return subtotal;
    }

    public void setSubtotal(BigDecimalFilter subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimalFilter getDiscount() {
        return discount;
    }

    public Optional<BigDecimalFilter> optionalDiscount() {
        return Optional.ofNullable(discount);
    }

    public BigDecimalFilter discount() {
        if (discount == null) {
            setDiscount(new BigDecimalFilter());
        }
        return discount;
    }

    public void setDiscount(BigDecimalFilter discount) {
        this.discount = discount;
    }

    public BigDecimalFilter getShippingCost() {
        return shippingCost;
    }

    public Optional<BigDecimalFilter> optionalShippingCost() {
        return Optional.ofNullable(shippingCost);
    }

    public BigDecimalFilter shippingCost() {
        if (shippingCost == null) {
            setShippingCost(new BigDecimalFilter());
        }
        return shippingCost;
    }

    public void setShippingCost(BigDecimalFilter shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimalFilter getTotalPrice() {
        return totalPrice;
    }

    public Optional<BigDecimalFilter> optionalTotalPrice() {
        return Optional.ofNullable(totalPrice);
    }

    public BigDecimalFilter totalPrice() {
        if (totalPrice == null) {
            setTotalPrice(new BigDecimalFilter());
        }
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public StringFilter getNotes() {
        return notes;
    }

    public Optional<StringFilter> optionalNotes() {
        return Optional.ofNullable(notes);
    }

    public StringFilter notes() {
        if (notes == null) {
            setNotes(new StringFilter());
        }
        return notes;
    }

    public void setNotes(StringFilter notes) {
        this.notes = notes;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getShippingAddressId() {
        return shippingAddressId;
    }

    public Optional<LongFilter> optionalShippingAddressId() {
        return Optional.ofNullable(shippingAddressId);
    }

    public LongFilter shippingAddressId() {
        if (shippingAddressId == null) {
            setShippingAddressId(new LongFilter());
        }
        return shippingAddressId;
    }

    public void setShippingAddressId(LongFilter shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerOrderCriteria that = (CustomerOrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderNumber, that.orderNumber) &&
            Objects.equals(orderDate, that.orderDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(subtotal, that.subtotal) &&
            Objects.equals(discount, that.discount) &&
            Objects.equals(shippingCost, that.shippingCost) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(notes, that.notes) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(shippingAddressId, that.shippingAddressId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            orderNumber,
            orderDate,
            status,
            subtotal,
            discount,
            shippingCost,
            totalPrice,
            notes,
            customerId,
            userId,
            shippingAddressId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrderNumber().map(f -> "orderNumber=" + f + ", ").orElse("") +
            optionalOrderDate().map(f -> "orderDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalSubtotal().map(f -> "subtotal=" + f + ", ").orElse("") +
            optionalDiscount().map(f -> "discount=" + f + ", ").orElse("") +
            optionalShippingCost().map(f -> "shippingCost=" + f + ", ").orElse("") +
            optionalTotalPrice().map(f -> "totalPrice=" + f + ", ").orElse("") +
            optionalNotes().map(f -> "notes=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalShippingAddressId().map(f -> "shippingAddressId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
