package br.com.andre.service;

import br.com.andre.domain.criteria.PaymentCriteria;
import br.com.andre.repository.PaymentRepository;
import br.com.andre.service.dto.PaymentDTO;
import br.com.andre.service.mapper.PaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> save(PaymentDTO paymentDTO) {
        LOG.debug("Request to save Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO)).map(paymentMapper::toDto);
    }

    /**
     * Update a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> update(PaymentDTO paymentDTO) {
        LOG.debug("Request to update Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO)).map(paymentMapper::toDto);
    }

    /**
     * Partially update a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        LOG.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .flatMap(paymentRepository::save)
            .map(paymentMapper::toDto);
    }

    /**
     * Find payments by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> findByCriteria(PaymentCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Payments by Criteria");
        return paymentRepository.findByCriteria(criteria, pageable).map(paymentMapper::toDto);
    }

    /**
     * Find the count of payments by criteria.
     * @param criteria filtering criteria
     * @return the count of payments
     */
    public Mono<Long> countByCriteria(PaymentCriteria criteria) {
        LOG.debug("Request to get the count of all Payments by Criteria");
        return paymentRepository.countByCriteria(criteria);
    }

    /**
     * Get all the payments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<PaymentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return paymentRepository.findAllWithEagerRelationships(pageable).map(paymentMapper::toDto);
    }

    /**
     * Returns the number of payments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentRepository.count();
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentDTO> findOne(Long id) {
        LOG.debug("Request to get Payment : {}", id);
        return paymentRepository.findOneWithEagerRelationships(id).map(paymentMapper::toDto);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Payment : {}", id);
        return paymentRepository.deleteById(id);
    }
}
