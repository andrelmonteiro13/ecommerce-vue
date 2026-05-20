package br.com.andre.repository;

import br.com.andre.domain.ShoppingCartItem;
import br.com.andre.domain.criteria.ShoppingCartItemCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ShoppingCartItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoppingCartItemRepository extends ReactiveCrudRepository<ShoppingCartItem, Long>, ShoppingCartItemRepositoryInternal {
    Flux<ShoppingCartItem> findAllBy(Pageable pageable);

    @Override
    Mono<ShoppingCartItem> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ShoppingCartItem> findAllWithEagerRelationships();

    @Override
    Flux<ShoppingCartItem> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM shopping_cart_item entity WHERE entity.cart_id = :id")
    Flux<ShoppingCartItem> findByCart(Long id);

    @Query("SELECT * FROM shopping_cart_item entity WHERE entity.cart_id IS NULL")
    Flux<ShoppingCartItem> findAllWhereCartIsNull();

    @Query("SELECT * FROM shopping_cart_item entity WHERE entity.product_id = :id")
    Flux<ShoppingCartItem> findByProduct(Long id);

    @Query("SELECT * FROM shopping_cart_item entity WHERE entity.product_id IS NULL")
    Flux<ShoppingCartItem> findAllWhereProductIsNull();

    @Override
    <S extends ShoppingCartItem> Mono<S> save(S entity);

    @Override
    Flux<ShoppingCartItem> findAll();

    @Override
    Mono<ShoppingCartItem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ShoppingCartItemRepositoryInternal {
    <S extends ShoppingCartItem> Mono<S> save(S entity);

    Flux<ShoppingCartItem> findAllBy(Pageable pageable);

    Flux<ShoppingCartItem> findAll();

    Mono<ShoppingCartItem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ShoppingCartItem> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ShoppingCartItem> findByCriteria(ShoppingCartItemCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ShoppingCartItemCriteria criteria);

    Mono<ShoppingCartItem> findOneWithEagerRelationships(Long id);

    Flux<ShoppingCartItem> findAllWithEagerRelationships();

    Flux<ShoppingCartItem> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
