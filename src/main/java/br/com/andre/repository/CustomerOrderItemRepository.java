package br.com.andre.repository;

import br.com.andre.domain.CustomerOrderItem;
import br.com.andre.domain.criteria.CustomerOrderItemCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CustomerOrderItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerOrderItemRepository extends ReactiveCrudRepository<CustomerOrderItem, Long>, CustomerOrderItemRepositoryInternal {
    Flux<CustomerOrderItem> findAllBy(Pageable pageable);

    @Override
    Mono<CustomerOrderItem> findOneWithEagerRelationships(Long id);

    @Override
    Flux<CustomerOrderItem> findAllWithEagerRelationships();

    @Override
    Flux<CustomerOrderItem> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM customer_order_item entity WHERE entity.order_id = :id")
    Flux<CustomerOrderItem> findByOrder(Long id);

    @Query("SELECT * FROM customer_order_item entity WHERE entity.order_id IS NULL")
    Flux<CustomerOrderItem> findAllWhereOrderIsNull();

    @Query("SELECT * FROM customer_order_item entity WHERE entity.product_id = :id")
    Flux<CustomerOrderItem> findByProduct(Long id);

    @Query("SELECT * FROM customer_order_item entity WHERE entity.product_id IS NULL")
    Flux<CustomerOrderItem> findAllWhereProductIsNull();

    @Override
    <S extends CustomerOrderItem> Mono<S> save(S entity);

    @Override
    Flux<CustomerOrderItem> findAll();

    @Override
    Mono<CustomerOrderItem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CustomerOrderItemRepositoryInternal {
    <S extends CustomerOrderItem> Mono<S> save(S entity);

    Flux<CustomerOrderItem> findAllBy(Pageable pageable);

    Flux<CustomerOrderItem> findAll();

    Mono<CustomerOrderItem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CustomerOrderItem> findAllBy(Pageable pageable, Criteria criteria);
    Flux<CustomerOrderItem> findByCriteria(CustomerOrderItemCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CustomerOrderItemCriteria criteria);

    Mono<CustomerOrderItem> findOneWithEagerRelationships(Long id);

    Flux<CustomerOrderItem> findAllWithEagerRelationships();

    Flux<CustomerOrderItem> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
