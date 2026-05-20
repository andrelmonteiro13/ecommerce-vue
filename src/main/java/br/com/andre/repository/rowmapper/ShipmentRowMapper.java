package br.com.andre.repository.rowmapper;

import br.com.andre.domain.Shipment;
import br.com.andre.domain.enumeration.ShipmentStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Shipment}, with proper type conversions.
 */
@Service
public class ShipmentRowMapper implements BiFunction<Row, String, Shipment> {

    private final ColumnConverter converter;

    public ShipmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Shipment} stored in the database.
     */
    @Override
    public Shipment apply(Row row, String prefix) {
        Shipment entity = new Shipment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTrackingNumber(converter.fromRow(row, prefix + "_tracking_number", String.class));
        entity.setCarrier(converter.fromRow(row, prefix + "_carrier", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ShipmentStatus.class));
        entity.setShippedDate(converter.fromRow(row, prefix + "_shipped_date", Instant.class));
        entity.setDeliveredDate(converter.fromRow(row, prefix + "_delivered_date", Instant.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
