package br.com.andre.repository;

import br.com.andre.domain.ShoppingCart;
import br.com.andre.domain.criteria.ShoppingCartCriteria;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerRowMapper;
import br.com.andre.repository.rowmapper.ShoppingCartRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ShoppingCart entity.
 */
@SuppressWarnings("unused")
class ShoppingCartRepositoryInternalImpl extends SimpleR2dbcRepository<ShoppingCart, Long> implements ShoppingCartRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final ShoppingCartRowMapper shoppingcartMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("shopping_cart", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");

    public ShoppingCartRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        ShoppingCartRowMapper shoppingcartMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ShoppingCart.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.shoppingcartMapper = shoppingcartMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ShoppingCart> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ShoppingCart> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ShoppingCartSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ShoppingCart.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ShoppingCart> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ShoppingCart> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ShoppingCart> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ShoppingCart> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ShoppingCart> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ShoppingCart process(Row row, RowMetadata metadata) {
        ShoppingCart entity = shoppingcartMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends ShoppingCart> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ShoppingCart> findByCriteria(ShoppingCartCriteria shoppingCartCriteria, Pageable page) {
        return createQuery(page, buildConditions(shoppingCartCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ShoppingCartCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ShoppingCartCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getCreatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedDate(), entityTable.column("created_date"));
            }
            if (criteria.getUpdatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getUpdatedDate(), entityTable.column("updated_date"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
