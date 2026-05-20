package br.com.andre.repository.rowmapper;

import br.com.andre.domain.Product;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSlug(converter.fromRow(row, prefix + "_slug", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setPromotionalPrice(converter.fromRow(row, prefix + "_promotional_price", BigDecimal.class));
        entity.setSku(converter.fromRow(row, prefix + "_sku", String.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setStock(converter.fromRow(row, prefix + "_stock", Integer.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setUpdatedDate(converter.fromRow(row, prefix + "_updated_date", Instant.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        return entity;
    }
}
