package br.com.andre.repository.rowmapper;

import br.com.andre.domain.ShoppingCart;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ShoppingCart}, with proper type conversions.
 */
@Service
public class ShoppingCartRowMapper implements BiFunction<Row, String, ShoppingCart> {

    private final ColumnConverter converter;

    public ShoppingCartRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ShoppingCart} stored in the database.
     */
    @Override
    public ShoppingCart apply(Row row, String prefix) {
        ShoppingCart entity = new ShoppingCart();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setUpdatedDate(converter.fromRow(row, prefix + "_updated_date", Instant.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
