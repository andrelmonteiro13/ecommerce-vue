package br.com.andre.repository;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.criteria.CustomerOrderCriteria;
import br.com.andre.repository.rowmapper.AddressRowMapper;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerOrderRowMapper;
import br.com.andre.repository.rowmapper.CustomerRowMapper;
import br.com.andre.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the CustomerOrder entity.
 */
@SuppressWarnings("unused")
class CustomerOrderRepositoryInternalImpl extends SimpleR2dbcRepository<CustomerOrder, Long> implements CustomerOrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final UserRowMapper userMapper;
    private final AddressRowMapper addressMapper;
    private final CustomerOrderRowMapper customerorderMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("customer_order", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table shippingAddressTable = Table.aliased("address", "shippingAddress");

    public CustomerOrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        UserRowMapper userMapper,
        AddressRowMapper addressMapper,
        CustomerOrderRowMapper customerorderMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CustomerOrder.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
        this.customerorderMapper = customerorderMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<CustomerOrder> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CustomerOrder> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CustomerOrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(AddressSqlHelper.getColumns(shippingAddressTable, "shippingAddress"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(shippingAddressTable)
            .on(Column.create("shipping_address_id", entityTable))
            .equals(Column.create("id", shippingAddressTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CustomerOrder.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CustomerOrder> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CustomerOrder> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<CustomerOrder> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<CustomerOrder> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<CustomerOrder> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private CustomerOrder process(Row row, RowMetadata metadata) {
        CustomerOrder entity = customerorderMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setUser(userMapper.apply(row, "user"));
        entity.setShippingAddress(addressMapper.apply(row, "shippingAddress"));
        return entity;
    }

    @Override
    public <S extends CustomerOrder> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<CustomerOrder> findByCriteria(CustomerOrderCriteria customerOrderCriteria, Pageable page) {
        return createQuery(page, buildConditions(customerOrderCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CustomerOrderCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CustomerOrderCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getOrderNumber() != null) {
                builder.buildFilterConditionForField(criteria.getOrderNumber(), entityTable.column("order_number"));
            }
            if (criteria.getOrderDate() != null) {
                builder.buildFilterConditionForField(criteria.getOrderDate(), entityTable.column("order_date"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getSubtotal() != null) {
                builder.buildFilterConditionForField(criteria.getSubtotal(), entityTable.column("subtotal"));
            }
            if (criteria.getDiscount() != null) {
                builder.buildFilterConditionForField(criteria.getDiscount(), entityTable.column("discount"));
            }
            if (criteria.getShippingCost() != null) {
                builder.buildFilterConditionForField(criteria.getShippingCost(), entityTable.column("shipping_cost"));
            }
            if (criteria.getTotalPrice() != null) {
                builder.buildFilterConditionForField(criteria.getTotalPrice(), entityTable.column("total_price"));
            }
            if (criteria.getNotes() != null) {
                builder.buildFilterConditionForField(criteria.getNotes(), entityTable.column("notes"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
            if (criteria.getUserId() != null) {
                builder.buildFilterConditionForField(criteria.getUserId(), userTable.column("id"));
            }
            if (criteria.getShippingAddressId() != null) {
                builder.buildFilterConditionForField(criteria.getShippingAddressId(), shippingAddressTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
