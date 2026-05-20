package br.com.andre.domain;

import br.com.andre.domain.enumeration.ShipmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Shipment.
 */
@Table("shipment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Shipment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(max = 100)
    @Column("tracking_number")
    private String trackingNumber;

    @Size(max = 100)
    @Column("carrier")
    private String carrier;

    @NotNull(message = "must not be null")
    @Column("status")
    private ShipmentStatus status;

    @Column("shipped_date")
    private Instant shippedDate;

    @Column("delivered_date")
    private Instant deliveredDate;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "user", "shippingAddress" }, allowSetters = true)
    private CustomerOrder order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public Shipment trackingNumber(String trackingNumber) {
        this.setTrackingNumber(trackingNumber);
        return this;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return this.carrier;
    }

    public Shipment carrier(String carrier) {
        this.setCarrier(carrier);
        return this;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public ShipmentStatus getStatus() {
        return this.status;
    }

    public Shipment status(ShipmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Instant getShippedDate() {
        return this.shippedDate;
    }

    public Shipment shippedDate(Instant shippedDate) {
        this.setShippedDate(shippedDate);
        return this;
    }

    public void setShippedDate(Instant shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Instant getDeliveredDate() {
        return this.deliveredDate;
    }

    public Shipment deliveredDate(Instant deliveredDate) {
        this.setDeliveredDate(deliveredDate);
        return this;
    }

    public void setDeliveredDate(Instant deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public CustomerOrder getOrder() {
        return this.order;
    }

    public void setOrder(CustomerOrder customerOrder) {
        this.order = customerOrder;
        this.orderId = customerOrder != null ? customerOrder.getId() : null;
    }

    public Shipment order(CustomerOrder customerOrder) {
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
        if (!(o instanceof Shipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Shipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + getId() +
            ", trackingNumber='" + getTrackingNumber() + "'" +
            ", carrier='" + getCarrier() + "'" +
            ", status='" + getStatus() + "'" +
            ", shippedDate='" + getShippedDate() + "'" +
            ", deliveredDate='" + getDeliveredDate() + "'" +
            "}";
    }
}
