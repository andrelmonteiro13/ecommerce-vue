package br.com.andre.repository.rowmapper;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.enumeration.CustomerOrderStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CustomerOrder}, with proper type conversions.
 */
@Service
public class CustomerOrderRowMapper implements BiFunction<Row, String, CustomerOrder> {

    private final ColumnConverter converter;

    public CustomerOrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CustomerOrder} stored in the database.
     */
    @Override
    public CustomerOrder apply(Row row, String prefix) {
        CustomerOrder entity = new CustomerOrder();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOrderNumber(converter.fromRow(row, prefix + "_order_number", String.class));
        entity.setOrderDate(converter.fromRow(row, prefix + "_order_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CustomerOrderStatus.class));
        entity.setSubtotal(converter.fromRow(row, prefix + "_subtotal", BigDecimal.class));
        entity.setDiscount(converter.fromRow(row, prefix + "_discount", BigDecimal.class));
        entity.setShippingCost(converter.fromRow(row, prefix + "_shipping_cost", BigDecimal.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setNotes(converter.fromRow(row, prefix + "_notes", String.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setShippingAddressId(converter.fromRow(row, prefix + "_shipping_address_id", Long.class));
        return entity;
    }
}
