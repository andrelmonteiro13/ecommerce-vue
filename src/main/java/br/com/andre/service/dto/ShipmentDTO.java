package br.com.andre.service.dto;

import br.com.andre.domain.enumeration.ShipmentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.Shipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShipmentDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String trackingNumber;

    @Size(max = 100)
    private String carrier;

    @NotNull(message = "must not be null")
    private ShipmentStatus status;

    private Instant shippedDate;

    private Instant deliveredDate;

    private CustomerOrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Instant getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Instant shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Instant getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Instant deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public CustomerOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CustomerOrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShipmentDTO)) {
            return false;
        }

        ShipmentDTO shipmentDTO = (ShipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShipmentDTO{" +
            "id=" + getId() +
            ", trackingNumber='" + getTrackingNumber() + "'" +
            ", carrier='" + getCarrier() + "'" +
            ", status='" + getStatus() + "'" +
            ", shippedDate='" + getShippedDate() + "'" +
            ", deliveredDate='" + getDeliveredDate() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
