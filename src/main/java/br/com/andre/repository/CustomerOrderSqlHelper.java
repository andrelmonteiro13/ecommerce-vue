package br.com.andre.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CustomerOrderSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("order_number", table, columnPrefix + "_order_number"));
        columns.add(Column.aliased("order_date", table, columnPrefix + "_order_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("subtotal", table, columnPrefix + "_subtotal"));
        columns.add(Column.aliased("discount", table, columnPrefix + "_discount"));
        columns.add(Column.aliased("shipping_cost", table, columnPrefix + "_shipping_cost"));
        columns.add(Column.aliased("total_price", table, columnPrefix + "_total_price"));
        columns.add(Column.aliased("notes", table, columnPrefix + "_notes"));

        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("shipping_address_id", table, columnPrefix + "_shipping_address_id"));
        return columns;
    }
}
