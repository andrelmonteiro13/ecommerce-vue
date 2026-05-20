package br.com.andre.web.rest;

import br.com.andre.domain.criteria.ShoppingCartCriteria;
import br.com.andre.repository.ShoppingCartRepository;
import br.com.andre.service.ShoppingCartService;
import br.com.andre.service.dto.ShoppingCartDTO;
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
 * REST controller for managing {@link br.com.andre.domain.ShoppingCart}.
 */
@RestController
@RequestMapping("/api/shopping-carts")
public class ShoppingCartResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartResource.class);

    private static final String ENTITY_NAME = "shoppingCart";

    @Value("${jhipster.clientApp.name:ecommerce}")
    private String applicationName;

    private final ShoppingCartService shoppingCartService;

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartResource(ShoppingCartService shoppingCartService, ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartService = shoppingCartService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    /**
     * {@code POST  /shopping-carts} : Create a new shoppingCart.
     *
     * @param shoppingCartDTO the shoppingCartDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoppingCartDTO, or with status {@code 400 (Bad Request)} if the shoppingCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ShoppingCartDTO>> createShoppingCart(@Valid @RequestBody ShoppingCartDTO shoppingCartDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ShoppingCart : {}", shoppingCartDTO);
        if (shoppingCartDTO.getId() != null) {
            throw new BadRequestAlertException("A new shoppingCart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return shoppingCartService
            .save(shoppingCartDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/shopping-carts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /shopping-carts/:id} : Updates an existing shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to save.
     * @param shoppingCartDTO the shoppingCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ShoppingCartDTO>> updateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoppingCartDTO shoppingCartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ShoppingCart : {}, {}", id, shoppingCartDTO);
        if (shoppingCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return shoppingCartService
                    .update(shoppingCartDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /shopping-carts/:id} : Partial updates given fields of an existing shoppingCart, field will ignore if it is null
     *
     * @param id the id of the shoppingCartDTO to save.
     * @param shoppingCartDTO the shoppingCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shoppingCartDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ShoppingCartDTO>> partialUpdateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoppingCartDTO shoppingCartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ShoppingCart partially : {}, {}", id, shoppingCartDTO);
        if (shoppingCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ShoppingCartDTO> result = shoppingCartService.partialUpdate(shoppingCartDTO);

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
     * {@code GET  /shopping-carts} : get all the Shopping Carts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Shopping Carts in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ShoppingCartDTO>>> getAllShoppingCarts(
        ShoppingCartCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ShoppingCarts by criteria: {}", criteria);
        return shoppingCartService
            .countByCriteria(criteria)
            .zipWith(shoppingCartService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /shopping-carts/count} : count all the shoppingCarts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countShoppingCarts(ShoppingCartCriteria criteria) {
        LOG.debug("REST request to count ShoppingCarts by criteria: {}", criteria);
        return shoppingCartService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /shopping-carts/:id} : get the "id" shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoppingCartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ShoppingCartDTO>> getShoppingCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ShoppingCart : {}", id);
        Mono<ShoppingCartDTO> shoppingCartDTO = shoppingCartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shoppingCartDTO);
    }

    /**
     * {@code DELETE  /shopping-carts/:id} : delete the "id" shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteShoppingCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ShoppingCart : {}", id);
        return shoppingCartService
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
