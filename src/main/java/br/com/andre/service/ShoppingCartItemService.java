package br.com.andre.service;

import br.com.andre.domain.criteria.ShoppingCartItemCriteria;
import br.com.andre.repository.ShoppingCartItemRepository;
import br.com.andre.service.dto.ShoppingCartItemDTO;
import br.com.andre.service.mapper.ShoppingCartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.ShoppingCartItem}.
 */
@Service
@Transactional
public class ShoppingCartItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartItemService.class);

    private final ShoppingCartItemRepository shoppingCartItemRepository;

    private final ShoppingCartItemMapper shoppingCartItemMapper;

    public ShoppingCartItemService(ShoppingCartItemRepository shoppingCartItemRepository, ShoppingCartItemMapper shoppingCartItemMapper) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.shoppingCartItemMapper = shoppingCartItemMapper;
    }

    /**
     * Save a shoppingCartItem.
     *
     * @param shoppingCartItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartItemDTO> save(ShoppingCartItemDTO shoppingCartItemDTO) {
        LOG.debug("Request to save ShoppingCartItem : {}", shoppingCartItemDTO);
        return shoppingCartItemRepository.save(shoppingCartItemMapper.toEntity(shoppingCartItemDTO)).map(shoppingCartItemMapper::toDto);
    }

    /**
     * Update a shoppingCartItem.
     *
     * @param shoppingCartItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartItemDTO> update(ShoppingCartItemDTO shoppingCartItemDTO) {
        LOG.debug("Request to update ShoppingCartItem : {}", shoppingCartItemDTO);
        return shoppingCartItemRepository.save(shoppingCartItemMapper.toEntity(shoppingCartItemDTO)).map(shoppingCartItemMapper::toDto);
    }

    /**
     * Partially update a shoppingCartItem.
     *
     * @param shoppingCartItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ShoppingCartItemDTO> partialUpdate(ShoppingCartItemDTO shoppingCartItemDTO) {
        LOG.debug("Request to partially update ShoppingCartItem : {}", shoppingCartItemDTO);

        return shoppingCartItemRepository
            .findById(shoppingCartItemDTO.getId())
            .map(existingShoppingCartItem -> {
                shoppingCartItemMapper.partialUpdate(existingShoppingCartItem, shoppingCartItemDTO);

                return existingShoppingCartItem;
            })
            .flatMap(shoppingCartItemRepository::save)
            .map(shoppingCartItemMapper::toDto);
    }

    /**
     * Find shoppingCartItems by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ShoppingCartItemDTO> findByCriteria(ShoppingCartItemCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ShoppingCartItems by Criteria");
        return shoppingCartItemRepository.findByCriteria(criteria, pageable).map(shoppingCartItemMapper::toDto);
    }

    /**
     * Find the count of shoppingCartItems by criteria.
     * @param criteria filtering criteria
     * @return the count of shoppingCartItems
     */
    public Mono<Long> countByCriteria(ShoppingCartItemCriteria criteria) {
        LOG.debug("Request to get the count of all ShoppingCartItems by Criteria");
        return shoppingCartItemRepository.countByCriteria(criteria);
    }

    /**
     * Get all the shoppingCartItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ShoppingCartItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shoppingCartItemRepository.findAllWithEagerRelationships(pageable).map(shoppingCartItemMapper::toDto);
    }

    /**
     * Returns the number of shoppingCartItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return shoppingCartItemRepository.count();
    }

    /**
     * Get one shoppingCartItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ShoppingCartItemDTO> findOne(Long id) {
        LOG.debug("Request to get ShoppingCartItem : {}", id);
        return shoppingCartItemRepository.findOneWithEagerRelationships(id).map(shoppingCartItemMapper::toDto);
    }

    /**
     * Delete the shoppingCartItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ShoppingCartItem : {}", id);
        return shoppingCartItemRepository.deleteById(id);
    }
}
