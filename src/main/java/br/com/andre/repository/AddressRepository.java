package br.com.andre.repository;

import br.com.andre.domain.Address;
import br.com.andre.domain.criteria.AddressCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends ReactiveCrudRepository<Address, Long>, AddressRepositoryInternal {
    Flux<Address> findAllBy(Pageable pageable);

    @Override
    Mono<Address> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Address> findAllWithEagerRelationships();

    @Override
    Flux<Address> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM address entity WHERE entity.customer_id = :id")
    Flux<Address> findByCustomer(Long id);

    @Query("SELECT * FROM address entity WHERE entity.customer_id IS NULL")
    Flux<Address> findAllWhereCustomerIsNull();

    @Override
    <S extends Address> Mono<S> save(S entity);

    @Override
    Flux<Address> findAll();

    @Override
    Mono<Address> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AddressRepositoryInternal {
    <S extends Address> Mono<S> save(S entity);

    Flux<Address> findAllBy(Pageable pageable);

    Flux<Address> findAll();

    Mono<Address> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Address> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Address> findByCriteria(AddressCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(AddressCriteria criteria);

    Mono<Address> findOneWithEagerRelationships(Long id);

    Flux<Address> findAllWithEagerRelationships();

    Flux<Address> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
