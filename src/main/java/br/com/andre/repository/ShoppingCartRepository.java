package br.com.andre.repository;

import br.com.andre.domain.ShoppingCart;
import br.com.andre.domain.criteria.ShoppingCartCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ShoppingCart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoppingCartRepository extends ReactiveCrudRepository<ShoppingCart, Long>, ShoppingCartRepositoryInternal {
    Flux<ShoppingCart> findAllBy(Pageable pageable);

    @Override
    Mono<ShoppingCart> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ShoppingCart> findAllWithEagerRelationships();

    @Override
    Flux<ShoppingCart> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM shopping_cart entity WHERE entity.customer_id = :id")
    Flux<ShoppingCart> findByCustomer(Long id);

    @Query("SELECT * FROM shopping_cart entity WHERE entity.customer_id IS NULL")
    Flux<ShoppingCart> findAllWhereCustomerIsNull();

    @Override
    <S extends ShoppingCart> Mono<S> save(S entity);

    @Override
    Flux<ShoppingCart> findAll();

    @Override
    Mono<ShoppingCart> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ShoppingCartRepositoryInternal {
    <S extends ShoppingCart> Mono<S> save(S entity);

    Flux<ShoppingCart> findAllBy(Pageable pageable);

    Flux<ShoppingCart> findAll();

    Mono<ShoppingCart> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ShoppingCart> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ShoppingCart> findByCriteria(ShoppingCartCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ShoppingCartCriteria criteria);

    Mono<ShoppingCart> findOneWithEagerRelationships(Long id);

    Flux<ShoppingCart> findAllWithEagerRelationships();

    Flux<ShoppingCart> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
