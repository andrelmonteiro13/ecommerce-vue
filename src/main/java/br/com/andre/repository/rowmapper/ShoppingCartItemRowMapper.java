package br.com.andre.repository.rowmapper;

import br.com.andre.domain.ShoppingCartItem;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ShoppingCartItem}, with proper type conversions.
 */
@Service
public class ShoppingCartItemRowMapper implements BiFunction<Row, String, ShoppingCartItem> {

    private final ColumnConverter converter;

    public ShoppingCartItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ShoppingCartItem} stored in the database.
     */
    @Override
    public ShoppingCartItem apply(Row row, String prefix) {
        ShoppingCartItem entity = new ShoppingCartItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
