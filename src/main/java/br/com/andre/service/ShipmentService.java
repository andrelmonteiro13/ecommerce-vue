package br.com.andre.service;

import br.com.andre.domain.criteria.ShipmentCriteria;
import br.com.andre.repository.ShipmentRepository;
import br.com.andre.service.dto.ShipmentDTO;
import br.com.andre.service.mapper.ShipmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.Shipment}.
 */
@Service
@Transactional
public class ShipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentService.class);

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    public ShipmentService(ShipmentRepository shipmentRepository, ShipmentMapper shipmentMapper) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
    }

    /**
     * Save a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShipmentDTO> save(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to save Shipment : {}", shipmentDTO);
        return shipmentRepository.save(shipmentMapper.toEntity(shipmentDTO)).map(shipmentMapper::toDto);
    }

    /**
     * Update a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ShipmentDTO> update(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to update Shipment : {}", shipmentDTO);
        return shipmentRepository.save(shipmentMapper.toEntity(shipmentDTO)).map(shipmentMapper::toDto);
    }

    /**
     * Partially update a shipment.
     *
     * @param shipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ShipmentDTO> partialUpdate(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to partially update Shipment : {}", shipmentDTO);

        return shipmentRepository
            .findById(shipmentDTO.getId())
            .map(existingShipment -> {
                shipmentMapper.partialUpdate(existingShipment, shipmentDTO);

                return existingShipment;
            })
            .flatMap(shipmentRepository::save)
            .map(shipmentMapper::toDto);
    }

    /**
     * Find shipments by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ShipmentDTO> findByCriteria(ShipmentCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Shipments by Criteria");
        return shipmentRepository.findByCriteria(criteria, pageable).map(shipmentMapper::toDto);
    }

    /**
     * Find the count of shipments by criteria.
     * @param criteria filtering criteria
     * @return the count of shipments
     */
    public Mono<Long> countByCriteria(ShipmentCriteria criteria) {
        LOG.debug("Request to get the count of all Shipments by Criteria");
        return shipmentRepository.countByCriteria(criteria);
    }

    /**
     * Get all the shipments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ShipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shipmentRepository.findAllWithEagerRelationships(pageable).map(shipmentMapper::toDto);
    }

    /**
     * Returns the number of shipments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return shipmentRepository.count();
    }

    /**
     * Get one shipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ShipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Shipment : {}", id);
        return shipmentRepository.findOneWithEagerRelationships(id).map(shipmentMapper::toDto);
    }

    /**
     * Delete the shipment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Shipment : {}", id);
        return shipmentRepository.deleteById(id);
    }
}
