package br.com.andre.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ShipmentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tracking_number", table, columnPrefix + "_tracking_number"));
        columns.add(Column.aliased("carrier", table, columnPrefix + "_carrier"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("shipped_date", table, columnPrefix + "_shipped_date"));
        columns.add(Column.aliased("delivered_date", table, columnPrefix + "_delivered_date"));

        columns.add(Column.aliased("order_id", table, columnPrefix + "_order_id"));
        return columns;
    }
}
