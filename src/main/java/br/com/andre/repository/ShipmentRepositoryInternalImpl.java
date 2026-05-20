package br.com.andre.repository;

import br.com.andre.domain.Shipment;
import br.com.andre.domain.criteria.ShipmentCriteria;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerOrderRowMapper;
import br.com.andre.repository.rowmapper.ShipmentRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Shipment entity.
 */
@SuppressWarnings("unused")
class ShipmentRepositoryInternalImpl extends SimpleR2dbcRepository<Shipment, Long> implements ShipmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerOrderRowMapper customerorderMapper;
    private final ShipmentRowMapper shipmentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("shipment", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("customer_order", "e_order");

    public ShipmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerOrderRowMapper customerorderMapper,
        ShipmentRowMapper shipmentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Shipment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerorderMapper = customerorderMapper;
        this.shipmentMapper = shipmentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Shipment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Shipment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ShipmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerOrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Shipment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Shipment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Shipment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Shipment> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Shipment> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Shipment> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Shipment process(Row row, RowMetadata metadata) {
        Shipment entity = shipmentMapper.apply(row, "e");
        entity.setOrder(customerorderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Shipment> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Shipment> findByCriteria(ShipmentCriteria shipmentCriteria, Pageable page) {
        return createQuery(page, buildConditions(shipmentCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ShipmentCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ShipmentCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTrackingNumber() != null) {
                builder.buildFilterConditionForField(criteria.getTrackingNumber(), entityTable.column("tracking_number"));
            }
            if (criteria.getCarrier() != null) {
                builder.buildFilterConditionForField(criteria.getCarrier(), entityTable.column("carrier"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getShippedDate() != null) {
                builder.buildFilterConditionForField(criteria.getShippedDate(), entityTable.column("shipped_date"));
            }
            if (criteria.getDeliveredDate() != null) {
                builder.buildFilterConditionForField(criteria.getDeliveredDate(), entityTable.column("delivered_date"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
