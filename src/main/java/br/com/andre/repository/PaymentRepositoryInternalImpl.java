package br.com.andre.repository;

import br.com.andre.domain.Payment;
import br.com.andre.domain.criteria.PaymentCriteria;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerOrderRowMapper;
import br.com.andre.repository.rowmapper.PaymentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Payment entity.
 */
@SuppressWarnings("unused")
class PaymentRepositoryInternalImpl extends SimpleR2dbcRepository<Payment, Long> implements PaymentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerOrderRowMapper customerorderMapper;
    private final PaymentRowMapper paymentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("payment", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("customer_order", "e_order");

    public PaymentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerOrderRowMapper customerorderMapper,
        PaymentRowMapper paymentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Payment.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerorderMapper = customerorderMapper;
        this.paymentMapper = paymentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Payment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Payment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PaymentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerOrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Payment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Payment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Payment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Payment> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Payment> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Payment> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Payment process(Row row, RowMetadata metadata) {
        Payment entity = paymentMapper.apply(row, "e");
        entity.setOrder(customerorderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Payment> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Payment> findByCriteria(PaymentCriteria paymentCriteria, Pageable page) {
        return createQuery(page, buildConditions(paymentCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(PaymentCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(PaymentCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getMethod() != null) {
                builder.buildFilterConditionForField(criteria.getMethod(), entityTable.column("method"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getAmount() != null) {
                builder.buildFilterConditionForField(criteria.getAmount(), entityTable.column("amount"));
            }
            if (criteria.getTransactionCode() != null) {
                builder.buildFilterConditionForField(criteria.getTransactionCode(), entityTable.column("transaction_code"));
            }
            if (criteria.getPaidDate() != null) {
                builder.buildFilterConditionForField(criteria.getPaidDate(), entityTable.column("paid_date"));
            }
            if (criteria.getOrderId() != null) {
                builder.buildFilterConditionForField(criteria.getOrderId(), orderTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
