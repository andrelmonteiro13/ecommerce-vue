package br.com.andre.domain.criteria;

import br.com.andre.domain.enumeration.ShipmentStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link br.com.andre.domain.Shipment} entity. This class is used
 * in {@link br.com.andre.web.rest.ShipmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShipmentStatus
     */
    public static class ShipmentStatusFilter extends Filter<ShipmentStatus> {

        public ShipmentStatusFilter() {}

        public ShipmentStatusFilter(ShipmentStatusFilter filter) {
            super(filter);
        }

        @Override
        public ShipmentStatusFilter copy() {
            return new ShipmentStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter trackingNumber;

    private StringFilter carrier;

    private ShipmentStatusFilter status;

    private InstantFilter shippedDate;

    private InstantFilter deliveredDate;

    private LongFilter orderId;

    private Boolean distinct;

    public ShipmentCriteria() {}

    public ShipmentCriteria(ShipmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.trackingNumber = other.optionalTrackingNumber().map(StringFilter::copy).orElse(null);
        this.carrier = other.optionalCarrier().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ShipmentStatusFilter::copy).orElse(null);
        this.shippedDate = other.optionalShippedDate().map(InstantFilter::copy).orElse(null);
        this.deliveredDate = other.optionalDeliveredDate().map(InstantFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ShipmentCriteria copy() {
        return new ShipmentCriteria(this);
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

    public StringFilter getTrackingNumber() {
        return trackingNumber;
    }

    public Optional<StringFilter> optionalTrackingNumber() {
        return Optional.ofNullable(trackingNumber);
    }

    public StringFilter trackingNumber() {
        if (trackingNumber == null) {
            setTrackingNumber(new StringFilter());
        }
        return trackingNumber;
    }

    public void setTrackingNumber(StringFilter trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public StringFilter getCarrier() {
        return carrier;
    }

    public Optional<StringFilter> optionalCarrier() {
        return Optional.ofNullable(carrier);
    }

    public StringFilter carrier() {
        if (carrier == null) {
            setCarrier(new StringFilter());
        }
        return carrier;
    }

    public void setCarrier(StringFilter carrier) {
        this.carrier = carrier;
    }

    public ShipmentStatusFilter getStatus() {
        return status;
    }

    public Optional<ShipmentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ShipmentStatusFilter status() {
        if (status == null) {
            setStatus(new ShipmentStatusFilter());
        }
        return status;
    }

    public void setStatus(ShipmentStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getShippedDate() {
        return shippedDate;
    }

    public Optional<InstantFilter> optionalShippedDate() {
        return Optional.ofNullable(shippedDate);
    }

    public InstantFilter shippedDate() {
        if (shippedDate == null) {
            setShippedDate(new InstantFilter());
        }
        return shippedDate;
    }

    public void setShippedDate(InstantFilter shippedDate) {
        this.shippedDate = shippedDate;
    }

    public InstantFilter getDeliveredDate() {
        return deliveredDate;
    }

    public Optional<InstantFilter> optionalDeliveredDate() {
        return Optional.ofNullable(deliveredDate);
    }

    public InstantFilter deliveredDate() {
        if (deliveredDate == null) {
            setDeliveredDate(new InstantFilter());
        }
        return deliveredDate;
    }

    public void setDeliveredDate(InstantFilter deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final ShipmentCriteria that = (ShipmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(trackingNumber, that.trackingNumber) &&
            Objects.equals(carrier, that.carrier) &&
            Objects.equals(status, that.status) &&
            Objects.equals(shippedDate, that.shippedDate) &&
            Objects.equals(deliveredDate, that.deliveredDate) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trackingNumber, carrier, status, shippedDate, deliveredDate, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTrackingNumber().map(f -> "trackingNumber=" + f + ", ").orElse("") +
            optionalCarrier().map(f -> "carrier=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalShippedDate().map(f -> "shippedDate=" + f + ", ").orElse("") +
            optionalDeliveredDate().map(f -> "deliveredDate=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
