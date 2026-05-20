package br.com.andre.web.rest;

import br.com.andre.domain.criteria.CustomerOrderCriteria;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.service.CustomerOrderService;
import br.com.andre.service.dto.CustomerOrderDTO;
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
 * REST controller for managing {@link br.com.andre.domain.CustomerOrder}.
 */
@RestController
@RequestMapping("/api/customer-orders")
public class CustomerOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderResource.class);

    private static final String ENTITY_NAME = "customerOrder";

    @Value("${jhipster.clientApp.name:ecommerce}")
    private String applicationName;

    private final CustomerOrderService customerOrderService;

    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderResource(CustomerOrderService customerOrderService, CustomerOrderRepository customerOrderRepository) {
        this.customerOrderService = customerOrderService;
        this.customerOrderRepository = customerOrderRepository;
    }

    /**
     * {@code POST  /customer-orders} : Create a new customerOrder.
     *
     * @param customerOrderDTO the customerOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerOrderDTO, or with status {@code 400 (Bad Request)} if the customerOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CustomerOrderDTO>> createCustomerOrder(@Valid @RequestBody CustomerOrderDTO customerOrderDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerOrder : {}", customerOrderDTO);
        if (customerOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return customerOrderService
            .save(customerOrderDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/customer-orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /customer-orders/:id} : Updates an existing customerOrder.
     *
     * @param id the id of the customerOrderDTO to save.
     * @param customerOrderDTO the customerOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerOrderDTO,
     * or with status {@code 400 (Bad Request)} if the customerOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerOrderDTO>> updateCustomerOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerOrderDTO customerOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerOrder : {}, {}", id, customerOrderDTO);
        if (customerOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return customerOrderService
                    .update(customerOrderDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /customer-orders/:id} : Partial updates given fields of an existing customerOrder, field will ignore if it is null
     *
     * @param id the id of the customerOrderDTO to save.
     * @param customerOrderDTO the customerOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerOrderDTO,
     * or with status {@code 400 (Bad Request)} if the customerOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CustomerOrderDTO>> partialUpdateCustomerOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomerOrderDTO customerOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerOrder partially : {}, {}", id, customerOrderDTO);
        if (customerOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CustomerOrderDTO> result = customerOrderService.partialUpdate(customerOrderDTO);

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
     * {@code GET  /customer-orders} : get all the Customer Orders.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Customer Orders in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CustomerOrderDTO>>> getAllCustomerOrders(
        CustomerOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get CustomerOrders by criteria: {}", criteria);
        return customerOrderService
            .countByCriteria(criteria)
            .zipWith(customerOrderService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /customer-orders/count} : count all the customerOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCustomerOrders(CustomerOrderCriteria criteria) {
        LOG.debug("REST request to count CustomerOrders by criteria: {}", criteria);
        return customerOrderService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /customer-orders/:id} : get the "id" customerOrder.
     *
     * @param id the id of the customerOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerOrderDTO>> getCustomerOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomerOrder : {}", id);
        Mono<CustomerOrderDTO> customerOrderDTO = customerOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerOrderDTO);
    }

    /**
     * {@code DELETE  /customer-orders/:id} : delete the "id" customerOrder.
     *
     * @param id the id of the customerOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomerOrder : {}", id);
        return customerOrderService
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
