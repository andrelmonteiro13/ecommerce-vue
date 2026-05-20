package br.com.andre.service;

import br.com.andre.domain.criteria.AddressCriteria;
import br.com.andre.repository.AddressRepository;
import br.com.andre.service.dto.AddressDTO;
import br.com.andre.service.mapper.AddressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.Address}.
 */
@Service
@Transactional
public class AddressService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Save a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> save(AddressDTO addressDTO) {
        LOG.debug("Request to save Address : {}", addressDTO);
        return addressRepository.save(addressMapper.toEntity(addressDTO)).map(addressMapper::toDto);
    }

    /**
     * Update a address.
     *
     * @param addressDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> update(AddressDTO addressDTO) {
        LOG.debug("Request to update Address : {}", addressDTO);
        return addressRepository.save(addressMapper.toEntity(addressDTO)).map(addressMapper::toDto);
    }

    /**
     * Partially update a address.
     *
     * @param addressDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AddressDTO> partialUpdate(AddressDTO addressDTO) {
        LOG.debug("Request to partially update Address : {}", addressDTO);

        return addressRepository
            .findById(addressDTO.getId())
            .map(existingAddress -> {
                addressMapper.partialUpdate(existingAddress, addressDTO);

                return existingAddress;
            })
            .flatMap(addressRepository::save)
            .map(addressMapper::toDto);
    }

    /**
     * Find addresses by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AddressDTO> findByCriteria(AddressCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Addresses by Criteria");
        return addressRepository.findByCriteria(criteria, pageable).map(addressMapper::toDto);
    }

    /**
     * Find the count of addresses by criteria.
     * @param criteria filtering criteria
     * @return the count of addresses
     */
    public Mono<Long> countByCriteria(AddressCriteria criteria) {
        LOG.debug("Request to get the count of all Addresses by Criteria");
        return addressRepository.countByCriteria(criteria);
    }

    /**
     * Get all the addresses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<AddressDTO> findAllWithEagerRelationships(Pageable pageable) {
        return addressRepository.findAllWithEagerRelationships(pageable).map(addressMapper::toDto);
    }

    /**
     * Returns the number of addresses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return addressRepository.count();
    }

    /**
     * Get one address by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AddressDTO> findOne(Long id) {
        LOG.debug("Request to get Address : {}", id);
        return addressRepository.findOneWithEagerRelationships(id).map(addressMapper::toDto);
    }

    /**
     * Delete the address by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Address : {}", id);
        return addressRepository.deleteById(id);
    }
}
