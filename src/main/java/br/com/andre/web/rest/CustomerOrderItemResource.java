package br.com.andre.web.rest;

import br.com.andre.domain.criteria.CustomerOrderItemCriteria;
import br.com.andre.repository.CustomerOrderItemRepository;
import br.com.andre.service.CustomerOrderItemService;
import br.com.andre.service.dto.CustomerOrderItemDTO;
import br.com.andre.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link br.com.andre.domain.CustomerOrderItem}.
 */
@RestController
@RequestMapping("/api/customer-order-items")
public class CustomerOrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderItemResource.class);

    private static final String ENTITY_NAME = "customerOrderItem";

    @Value("${jhipster.clientApp.name:ecommerce}")
    private String applicationName;

    private final CustomerOrderItemService customerOrderItemService;

    private final CustomerOrderItemRepository customerOrderItemRepository;

    public CustomerOrderItemResource(
        CustomerOrderItemService customerOrderItemService,
        CustomerOrderItemRepository customerOrderItemRepository
    ) {
        this.customerOrderItemService = customerOrderItemService;
        this.customerOrderItemRepository = customerOrderItemRepository;
    }

    /**
     * {@code POST  /customer-order-items} : Create a new customerOrderItem.
     *
     * @param customerOrderItemDTO the customerOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerOrderItemDTO, or with status {@code 400 (Bad Request)} if the customerOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CustomerOrderItemDTO>> createCustomerOrderItem(@Valid @RequestBody CustomerOrderItemDTO customerOrderItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerOrderItem : {}", customerOrderItemDTO);
        if (customerOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return customerOrderItemService
            .save(customerOrderItemDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/customer-order-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /customer-order-items/:id} : Updates an existing customerOrderItem.
     *
     * @param id the id of the customerOrderItemDTO to save.
     * @param customerOrderItemDTO the customerOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the customerOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerOrderItemDTO>> updateCustomerOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerOrderItemDTO customerOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerOrderItem : {}, {}", id, customerOrderItemDTO);
        if (customerOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerOrderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return customerOrderItemService
                    .update(customerOrderItemDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /customer-order-items/:id} : Partial updates given fields of an existing customerOrderItem, field will ignore if it is null
     *
     * @param id the id of the customerOrderItemDTO to save.
     * @param customerOrderItemDTO the customerOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the customerOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CustomerOrderItemDTO>> partialUpdateCustomerOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomerOrderItemDTO customerOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerOrderItem partially : {}, {}", id, customerOrderItemDTO);
        if (customerOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerOrderItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CustomerOrderItemDTO> result = customerOrderItemService.partialUpdate(customerOrderItemDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /customer-order-items} : get all the Customer Order Items.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Customer Order Items in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CustomerOrderItemDTO>>> getAllCustomerOrderItems(
        CustomerOrderItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get CustomerOrderItems by criteria: {}", criteria);
        return customerOrderItemService
            .countByCriteria(criteria)
            .zipWith(customerOrderItemService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /customer-order-items/count} : count all the customerOrderItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCustomerOrderItems(CustomerOrderItemCriteria criteria) {
        LOG.debug("REST request to count CustomerOrderItems by criteria: {}", criteria);
        return customerOrderItemService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /customer-order-items/:id} : get the "id" customerOrderItem.
     *
     * @param id the id of the customerOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerOrderItemDTO>> getCustomerOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomerOrderItem : {}", id);
        Mono<CustomerOrderItemDTO> customerOrderItemDTO = customerOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerOrderItemDTO);
    }

    /**
     * {@code DELETE  /customer-order-items/:id} : delete the "id" customerOrderItem.
     *
     * @param id the id of the customerOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomerOrderItem : {}", id);
        return customerOrderItemService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
