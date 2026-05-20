package br.com.andre.service;

import br.com.andre.domain.criteria.ShoppingCartCriteria;
import br.com.andre.repository.ShoppingCartRepository;
import br.com.andre.service.dto.ShoppingCartDTO;
import br.com.andre.service.mapper.ShoppingCartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.ShoppingCart}.
 */
@Service
@Transactional
public class ShoppingCartService {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartService.class);

    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ShoppingCartMapper shoppingCartMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    /**
     * Save a shoppingCart.
     *
     * @param shoppingCartDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartDTO> save(ShoppingCartDTO shoppingCartDTO) {
        LOG.debug("Request to save ShoppingCart : {}", shoppingCartDTO);
        return shoppingCartRepository.save(shoppingCartMapper.toEntity(shoppingCartDTO)).map(shoppingCartMapper::toDto);
    }

    /**
     * Update a shoppingCart.
     *
     * @param shoppingCartDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartDTO> update(ShoppingCartDTO shoppingCartDTO) {
        LOG.debug("Request to update ShoppingCart : {}", shoppingCartDTO);
        return shoppingCartRepository.save(shoppingCartMapper.toEntity(shoppingCartDTO)).map(shoppingCartMapper::toDto);
    }

    /**
     * Partially update a shoppingCart.
     *
     * @param shoppingCartDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartDTO> partialUpdate(ShoppingCartDTO shoppingCartDTO) {
        LOG.debug("Request to partially update ShoppingCart : {}", shoppingCartDTO);

        return shoppingCartRepository
            .findById(shoppingCartDTO.getId())
            .map(existingShoppingCart -> {
                shoppingCartMapper.partialUpdate(existingShoppingCart, shoppingCartDTO);

                return existingShoppingCart;
            })
            .flatMap(shoppingCartRepository::save)
            .map(shoppingCartMapper::toDto);
    }

    /**
     * Find shoppingCarts by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ShoppingCartDTO> findByCriteria(ShoppingCartCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ShoppingCarts by Criteria");
        return shoppingCartRepository.findByCriteria(criteria, pageable).map(shoppingCartMapper::toDto);
    }

    /**
     * Find the count of shoppingCarts by criteria.
     * @param criteria filtering criteria
     * @return the count of shoppingCarts
     */
    public Mono<Long> countByCriteria(ShoppingCartCriteria criteria) {
        LOG.debug("Request to get the count of all ShoppingCarts by Criteria");
        return shoppingCartRepository.countByCriteria(criteria);
    }

    /**
     * Get all the shoppingCarts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ShoppingCartDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shoppingCartRepository.findAllWithEagerRelationships(pageable).map(shoppingCartMapper::toDto);
    }

    /**
     * Returns the number of shoppingCarts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return shoppingCartRepository.count();
    }

    /**
     * Get one shoppingCart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ShoppingCartDTO> findOne(Long id) {
        LOG.debug("Request to get ShoppingCart : {}", id);
        return shoppingCartRepository.findOneWithEagerRelationships(id).map(shoppingCartMapper::toDto);
    }

    /**
     * Delete the shoppingCart by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ShoppingCart : {}", id);
        return shoppingCartRepository.deleteById(id);
    }
}
