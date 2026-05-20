package br.com.andre.service;

import br.com.andre.domain.criteria.CustomerOrderItemCriteria;
import br.com.andre.repository.CustomerOrderItemRepository;
import br.com.andre.service.dto.CustomerOrderItemDTO;
import br.com.andre.service.mapper.CustomerOrderItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.CustomerOrderItem}.
 */
@Service
@Transactional
public class CustomerOrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderItemService.class);

    private final CustomerOrderItemRepository customerOrderItemRepository;

    private final CustomerOrderItemMapper customerOrderItemMapper;

    public CustomerOrderItemService(
        CustomerOrderItemRepository customerOrderItemRepository,
        CustomerOrderItemMapper customerOrderItemMapper
    ) {
        this.customerOrderItemRepository = customerOrderItemRepository;
        this.customerOrderItemMapper = customerOrderItemMapper;
    }

    /**
     * Save a customerOrderItem.
     *
     * @param customerOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderItemDTO> save(CustomerOrderItemDTO customerOrderItemDTO) {
        LOG.debug("Request to save CustomerOrderItem : {}", customerOrderItemDTO);
        return customerOrderItemRepository.save(customerOrderItemMapper.toEntity(customerOrderItemDTO)).map(customerOrderItemMapper::toDto);
    }

    /**
     * Update a customerOrderItem.
     *
     * @param customerOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderItemDTO> update(CustomerOrderItemDTO customerOrderItemDTO) {
        LOG.debug("Request to update CustomerOrderItem : {}", customerOrderItemDTO);
        return customerOrderItemRepository.save(customerOrderItemMapper.toEntity(customerOrderItemDTO)).map(customerOrderItemMapper::toDto);
    }

    /**
     * Partially update a customerOrderItem.
     *
     * @param customerOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderItemDTO> partialUpdate(CustomerOrderItemDTO customerOrderItemDTO) {
        LOG.debug("Request to partially update CustomerOrderItem : {}", customerOrderItemDTO);

        return customerOrderItemRepository
            .findById(customerOrderItemDTO.getId())
            .map(existingCustomerOrderItem -> {
                customerOrderItemMapper.partialUpdate(existingCustomerOrderItem, customerOrderItemDTO);

                return existingCustomerOrderItem;
            })
            .flatMap(customerOrderItemRepository::save)
            .map(customerOrderItemMapper::toDto);
    }

    /**
     * Find customerOrderItems by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerOrderItemDTO> findByCriteria(CustomerOrderItemCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all CustomerOrderItems by Criteria");
        return customerOrderItemRepository.findByCriteria(criteria, pageable).map(customerOrderItemMapper::toDto);
    }

    /**
     * Find the count of customerOrderItems by criteria.
     * @param criteria filtering criteria
     * @return the count of customerOrderItems
     */
    public Mono<Long> countByCriteria(CustomerOrderItemCriteria criteria) {
        LOG.debug("Request to get the count of all CustomerOrderItems by Criteria");
        return customerOrderItemRepository.countByCriteria(criteria);
    }

    /**
     * Get all the customerOrderItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<CustomerOrderItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return customerOrderItemRepository.findAllWithEagerRelationships(pageable).map(customerOrderItemMapper::toDto);
    }

    /**
     * Returns the number of customerOrderItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerOrderItemRepository.count();
    }

    /**
     * Get one customerOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerOrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get CustomerOrderItem : {}", id);
        return customerOrderItemRepository.findOneWithEagerRelationships(id).map(customerOrderItemMapper::toDto);
    }

    /**
     * Delete the customerOrderItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete CustomerOrderItem : {}", id);
        return customerOrderItemRepository.deleteById(id);
    }
}
