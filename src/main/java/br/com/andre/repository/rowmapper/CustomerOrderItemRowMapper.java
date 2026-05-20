package br.com.andre.repository.rowmapper;

import br.com.andre.domain.CustomerOrderItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CustomerOrderItem}, with proper type conversions.
 */
@Service
public class CustomerOrderItemRowMapper implements BiFunction<Row, String, CustomerOrderItem> {

    private final ColumnConverter converter;

    public CustomerOrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CustomerOrderItem} stored in the database.
     */
    @Override
    public CustomerOrderItem apply(Row row, String prefix) {
        CustomerOrderItem entity = new CustomerOrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
