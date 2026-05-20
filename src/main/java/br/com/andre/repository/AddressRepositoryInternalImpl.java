package br.com.andre.repository;

import br.com.andre.domain.Address;
import br.com.andre.domain.criteria.AddressCriteria;
import br.com.andre.repository.rowmapper.AddressRowMapper;
import br.com.andre.repository.rowmapper.ColumnConverter;
import br.com.andre.repository.rowmapper.CustomerRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Address entity.
 */
@SuppressWarnings("unused")
class AddressRepositoryInternalImpl extends SimpleR2dbcRepository<Address, Long> implements AddressRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CustomerRowMapper customerMapper;
    private final AddressRowMapper addressMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("address", EntityManager.ENTITY_ALIAS);
    private static final Table customerTable = Table.aliased("customer", "customer");

    public AddressRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CustomerRowMapper customerMapper,
        AddressRowMapper addressMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Address.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Address> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Address> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AddressSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Address.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Address> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Address> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Address> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Address> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Address> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Address process(Row row, RowMetadata metadata) {
        Address entity = addressMapper.apply(row, "e");
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Address> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Address> findByCriteria(AddressCriteria addressCriteria, Pageable page) {
        return createQuery(page, buildConditions(addressCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AddressCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AddressCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStreet() != null) {
                builder.buildFilterConditionForField(criteria.getStreet(), entityTable.column("street"));
            }
            if (criteria.getNumber() != null) {
                builder.buildFilterConditionForField(criteria.getNumber(), entityTable.column("number"));
            }
            if (criteria.getComplement() != null) {
                builder.buildFilterConditionForField(criteria.getComplement(), entityTable.column("complement"));
            }
            if (criteria.getDistrict() != null) {
                builder.buildFilterConditionForField(criteria.getDistrict(), entityTable.column("district"));
            }
            if (criteria.getCity() != null) {
                builder.buildFilterConditionForField(criteria.getCity(), entityTable.column("city"));
            }
            if (criteria.getState() != null) {
                builder.buildFilterConditionForField(criteria.getState(), entityTable.column("state"));
            }
            if (criteria.getZipCode() != null) {
                builder.buildFilterConditionForField(criteria.getZipCode(), entityTable.column("zip_code"));
            }
            if (criteria.getCountry() != null) {
                builder.buildFilterConditionForField(criteria.getCountry(), entityTable.column("country"));
            }
            if (criteria.getCustomerId() != null) {
                builder.buildFilterConditionForField(criteria.getCustomerId(), customerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
