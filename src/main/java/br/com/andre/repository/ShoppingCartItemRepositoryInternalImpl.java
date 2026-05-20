package br.com.andre.repository;

import br.com.andre.domain.ShoppingCartItem;
import br.com.andre.domain.criteria.ShoppingCartItemCriteria;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.ProductRowMapper;
import br.com.andre.repository.rowmapper.ShoppingCartItemRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ShoppingCartItem entity.
 */
@SuppressWarnings("unused")
class ShoppingCartItemRepositoryInternalImpl
    extends SimpleR2dbcRepository<ShoppingCartItem, Long>
    implements ShoppingCartItemRepositoryInternal
{

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ShoppingCartRowMapper shoppingcartMapper;
    private final ProductRowMapper productMapper;
    private final ShoppingCartItemRowMapper shoppingcartitemMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("shopping_cart_item", EntityManager.ENTITY_ALIAS);
    private static final Table cartTable = Table.aliased("shopping_cart", "cart");
    private static final Table productTable = Table.aliased("product", "product");

    public ShoppingCartItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ShoppingCartRowMapper shoppingcartMapper,
        ProductRowMapper productMapper,
        ShoppingCartItemRowMapper shoppingcartitemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ShoppingCartItem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.shoppingcartMapper = shoppingcartMapper;
        this.productMapper = productMapper;
        this.shoppingcartitemMapper = shoppingcartitemMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ShoppingCartItem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ShoppingCartItem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ShoppingCartItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ShoppingCartSqlHelper.getColumns(cartTable, "cart"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ShoppingCartItem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ShoppingCartItem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ShoppingCartItem> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<ShoppingCartItem> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ShoppingCartItem> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ShoppingCartItem> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ShoppingCartItem process(Row row, RowMetadata metadata) {
        ShoppingCartItem entity = shoppingcartitemMapper.apply(row, "e");
        entity.setCart(shoppingcartMapper.apply(row, "cart"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends ShoppingCartItem> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ShoppingCartItem> findByCriteria(ShoppingCartItemCriteria shoppingCartItemCriteria, Pageable page) {
        return createQuery(page, buildConditions(shoppingCartItemCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ShoppingCartItemCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ShoppingCartItemCriteria criteria) {
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
            if (criteria.getCartId() != null) {
                builder.buildFilterConditionForField(criteria.getCartId(), cartTable.column("id"));
            }
            if (criteria.getProductId() != null) {
                builder.buildFilterConditionForField(criteria.getProductId(), productTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
