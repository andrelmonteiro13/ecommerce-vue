package br.com.andre.service;

import br.com.andre.domain.criteria.CustomerOrderCriteria;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.mapper.CustomerOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.CustomerOrder}.
 */
@Service
@Transactional
public class CustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderService.class);

    private final CustomerOrderRepository customerOrderRepository;

    private final CustomerOrderMapper customerOrderMapper;

    public CustomerOrderService(CustomerOrderRepository customerOrderRepository, CustomerOrderMapper customerOrderMapper) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderMapper = customerOrderMapper;
    }

    /**
     * Save a customerOrder.
     *
     * @param customerOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderDTO> save(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to save CustomerOrder : {}", customerOrderDTO);
        return customerOrderRepository.save(customerOrderMapper.toEntity(customerOrderDTO)).map(customerOrderMapper::toDto);
    }

    /**
     * Update a customerOrder.
     *
     * @param customerOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderDTO> update(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to update CustomerOrder : {}", customerOrderDTO);
        return customerOrderRepository.save(customerOrderMapper.toEntity(customerOrderDTO)).map(customerOrderMapper::toDto);
    }

    /**
     * Partially update a customerOrder.
     *
     * @param customerOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerOrderDTO> partialUpdate(CustomerOrderDTO customerOrderDTO) {
        LOG.debug("Request to partially update CustomerOrder : {}", customerOrderDTO);

        return customerOrderRepository
            .findById(customerOrderDTO.getId())
            .map(existingCustomerOrder -> {
                customerOrderMapper.partialUpdate(existingCustomerOrder, customerOrderDTO);

                return existingCustomerOrder;
            })
            .flatMap(customerOrderRepository::save)
            .map(customerOrderMapper::toDto);
    }

    /**
     * Find customerOrders by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerOrderDTO> findByCriteria(CustomerOrderCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all CustomerOrders by Criteria");
        return customerOrderRepository.findByCriteria(criteria, pageable).map(customerOrderMapper::toDto);
    }

    /**
     * Find the count of customerOrders by criteria.
     * @param criteria filtering criteria
     * @return the count of customerOrders
     */
    public Mono<Long> countByCriteria(CustomerOrderCriteria criteria) {
        LOG.debug("Request to get the count of all CustomerOrders by Criteria");
        return customerOrderRepository.countByCriteria(criteria);
    }

    /**
     * Get all the customerOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<CustomerOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return customerOrderRepository.findAllWithEagerRelationships(pageable).map(customerOrderMapper::toDto);
    }

    /**
     * Returns the number of customerOrders available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerOrderRepository.count();
    }

    /**
     * Get one customerOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerOrderDTO> findOne(Long id) {
        LOG.debug("Request to get CustomerOrder : {}", id);
        return customerOrderRepository.findOneWithEagerRelationships(id).map(customerOrderMapper::toDto);
    }

    /**
     * Delete the customerOrder by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete CustomerOrder : {}", id);
        return customerOrderRepository.deleteById(id);
    }
}
