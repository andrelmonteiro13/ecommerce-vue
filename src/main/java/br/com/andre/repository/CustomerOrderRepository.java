package br.com.andre.repository;

import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.criteria.CustomerOrderCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CustomerOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, Long>, CustomerOrderRepositoryInternal {
    Flux<CustomerOrder> findAllBy(Pageable pageable);

    @Override
    Mono<CustomerOrder> findOneWithEagerRelationships(Long id);

    @Override
    Flux<CustomerOrder> findAllWithEagerRelationships();

    @Override
    Flux<CustomerOrder> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM customer_order entity WHERE entity.customer_id = :id")
    Flux<CustomerOrder> findByCustomer(Long id);

    @Query("SELECT * FROM customer_order entity WHERE entity.customer_id IS NULL")
    Flux<CustomerOrder> findAllWhereCustomerIsNull();

    @Query("SELECT * FROM customer_order entity WHERE entity.user_id = :id")
    Flux<CustomerOrder> findByUser(Long id);

    @Query("SELECT * FROM customer_order entity WHERE entity.user_id IS NULL")
    Flux<CustomerOrder> findAllWhereUserIsNull();

    @Query("SELECT * FROM customer_order entity WHERE entity.shipping_address_id = :id")
    Flux<CustomerOrder> findByShippingAddress(Long id);

    @Query("SELECT * FROM customer_order entity WHERE entity.shipping_address_id IS NULL")
    Flux<CustomerOrder> findAllWhereShippingAddressIsNull();

    @Override
    <S extends CustomerOrder> Mono<S> save(S entity);

    @Override
    Flux<CustomerOrder> findAll();

    @Override
    Mono<CustomerOrder> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CustomerOrderRepositoryInternal {
    <S extends CustomerOrder> Mono<S> save(S entity);

    Flux<CustomerOrder> findAllBy(Pageable pageable);

    Flux<CustomerOrder> findAll();

    Mono<CustomerOrder> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CustomerOrder> findAllBy(Pageable pageable, Criteria criteria);
    Flux<CustomerOrder> findByCriteria(CustomerOrderCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CustomerOrderCriteria criteria);

    Mono<CustomerOrder> findOneWithEagerRelationships(Long id);

    Flux<CustomerOrder> findAllWithEagerRelationships();

    Flux<CustomerOrder> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
