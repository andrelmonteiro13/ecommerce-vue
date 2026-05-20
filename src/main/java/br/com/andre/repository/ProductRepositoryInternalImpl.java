package br.com.andre.repository;

import br.com.andre.domain.Product;
import br.com.andre.domain.criteria.ProductCriteria;
import br.com.andre.repository.rowmapper.CategoryRowMapper;
import br.com.andre.repository.rowmapper.ColumnConverter;
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
 * Spring Data R2DBC custom repository implementation for the Product entity.
 */
@SuppressWarnings("unused")
class ProductRepositoryInternalImpl extends SimpleR2dbcRepository<Product, Long> implements ProductRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoryRowMapper categoryMapper;
    private final ProductRowMapper productMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("product", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("category", "category");

    public ProductRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoryRowMapper categoryMapper,
        ProductRowMapper productMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Product.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Product> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Product> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Product.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Product> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Product> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Product> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Product> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Product> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Product process(Row row, RowMetadata metadata) {
        Product entity = productMapper.apply(row, "e");
        entity.setCategory(categoryMapper.apply(row, "category"));
        return entity;
    }

    @Override
    public <S extends Product> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Product> findByCriteria(ProductCriteria productCriteria, Pageable page) {
        return createQuery(page, buildConditions(productCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProductCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ProductCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getSlug() != null) {
                builder.buildFilterConditionForField(criteria.getSlug(), entityTable.column("slug"));
            }
            if (criteria.getPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPrice(), entityTable.column("price"));
            }
            if (criteria.getPromotionalPrice() != null) {
                builder.buildFilterConditionForField(criteria.getPromotionalPrice(), entityTable.column("promotional_price"));
            }
            if (criteria.getSku() != null) {
                builder.buildFilterConditionForField(criteria.getSku(), entityTable.column("sku"));
            }
            if (criteria.getImageUrl() != null) {
                builder.buildFilterConditionForField(criteria.getImageUrl(), entityTable.column("image_url"));
            }
            if (criteria.getStock() != null) {
                builder.buildFilterConditionForField(criteria.getStock(), entityTable.column("stock"));
            }
            if (criteria.getActive() != null) {
                builder.buildFilterConditionForField(criteria.getActive(), entityTable.column("active"));
            }
            if (criteria.getCreatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedDate(), entityTable.column("created_date"));
            }
            if (criteria.getUpdatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getUpdatedDate(), entityTable.column("updated_date"));
            }
            if (criteria.getCategoryId() != null) {
                builder.buildFilterConditionForField(criteria.getCategoryId(), categoryTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
