package br.com.andre.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("slug", table, columnPrefix + "_slug"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("promotional_price", table, columnPrefix + "_promotional_price"));
        columns.add(Column.aliased("sku", table, columnPrefix + "_sku"));
        columns.add(Column.aliased("image_url", table, columnPrefix + "_image_url"));
        columns.add(Column.aliased("stock", table, columnPrefix + "_stock"));
        columns.add(Column.aliased("active", table, columnPrefix + "_active"));
        columns.add(Column.aliased("created_date", table, columnPrefix + "_created_date"));
        columns.add(Column.aliased("updated_date", table, columnPrefix + "_updated_date"));

        columns.add(Column.aliased("category_id", table, columnPrefix + "_category_id"));
        return columns;
    }
}
