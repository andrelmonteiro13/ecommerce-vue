package br.com.andre.repository;

import br.com.andre.domain.CustomerOrderItem;
import br.com.andre.domain.criteria.CustomerOrderItemCriteria;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerOrderItemRowMapper;
import br.com.andre.repository.rowmapper.CustomerOrderRowMapper;
import br.com.andre.repository.rowmapper.ProductRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the CustomerOrderItem entity.
 */
@SuppressWarnings("unused")
class CustomerOrderItemRepositoryInternalImpl
    extends SimpleR2dbcRepository<CustomerOrderItem, Long>
    implements CustomerOrderItemRepositoryInternal
{

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerOrderRowMapper customerorderMapper;
    private final ProductRowMapper productMapper;
    private final CustomerOrderItemRowMapper customerorderitemMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("customer_order_item", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("customer_order", "e_order");
    private static final Table productTable = Table.aliased("product", "product");

    public CustomerOrderItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerOrderRowMapper customerorderMapper,
        ProductRowMapper productMapper,
        CustomerOrderItemRowMapper customerorderitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CustomerOrderItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerorderMapper = customerorderMapper;
        this.productMapper = productMapper;
        this.customerorderitemMapper = customerorderitemMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<CustomerOrderItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CustomerOrderItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerOrderItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerOrderSqlHelper.getColumns(orderTable, "order"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CustomerOrderItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CustomerOrderItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CustomerOrderItem> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<CustomerOrderItem> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<CustomerOrderItem> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<CustomerOrderItem> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private CustomerOrderItem process(Row row, RowMetadata metadata) {
        CustomerOrderItem entity = customerorderitemMapper.apply(row, "e");
        entity.setOrder(customerorderMapper.apply(row, "order"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends CustomerOrderItem> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<CustomerOrderItem> findByCriteria(CustomerOrderItemCriteria customerOrderItemCriteria, Pageable page) {
        return createQuery(page, buildConditions(customerOrderItemCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CustomerOrderItemCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CustomerOrderItemCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getQuantity() != null) {
                builder.buildFilterConditionForField(criteria.getQuantity(), entityTable.column("quantity"));
            }
            if (criteria.getUnitPrice() != null) {
                builder.buildFilterConditionForField(criteria.getUnitPrice(), entityTable.column("unit_price"));
            }
            if (criteria.getTotalPrice() != null) {
                builder.buildFilterConditionForField(criteria.getTotalPrice(), entityTable.column("total_price"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
            if (criteria.getProductId() != null) {
                builder.buildFilterConditionForField(criteria.getProductId(), productTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
