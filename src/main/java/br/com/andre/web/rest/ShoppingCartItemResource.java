package br.com.andre.web.rest;

import br.com.andre.domain.criteria.ShoppingCartItemCriteria;
import br.com.andre.repository.ShoppingCartItemRepository;
import br.com.andre.service.ShoppingCartItemService;
import br.com.andre.service.dto.ShoppingCartItemDTO;
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
 * REST controller for managing {@link br.com.andre.domain.ShoppingCartItem}.
 */
@RestController
@RequestMapping("/api/shopping-cart-items")
public class ShoppingCartItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartItemResource.class);

    private static final String ENTITY_NAME = "shoppingCartItem";

    @Value("${jhipster.clientApp.name:ecommerce}")
    private String applicationName;

    private final ShoppingCartItemService shoppingCartItemService;

    private final ShoppingCartItemRepository shoppingCartItemRepository;

    public ShoppingCartItemResource(
        ShoppingCartItemService shoppingCartItemService,
        ShoppingCartItemRepository shoppingCartItemRepository
    ) {
        this.shoppingCartItemService = shoppingCartItemService;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    /**
     * {@code POST  /shopping-cart-items} : Create a new shoppingCartItem.
     *
     * @param shoppingCartItemDTO the shoppingCartItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoppingCartItemDTO, or with status {@code 400 (Bad Request)} if the shoppingCartItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ShoppingCartItemDTO>> createShoppingCartItem(@Valid @RequestBody ShoppingCartItemDTO shoppingCartItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ShoppingCartItem : {}", shoppingCartItemDTO);
        if (shoppingCartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new shoppingCartItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return shoppingCartItemService
            .save(shoppingCartItemDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/shopping-cart-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /shopping-cart-items/:id} : Updates an existing shoppingCartItem.
     *
     * @param id the id of the shoppingCartItemDTO to save.
     * @param shoppingCartItemDTO the shoppingCartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartItemDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ShoppingCartItemDTO>> updateShoppingCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoppingCartItemDTO shoppingCartItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ShoppingCartItem : {}, {}", id, shoppingCartItemDTO);
        if (shoppingCartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return shoppingCartItemService
                    .update(shoppingCartItemDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /shopping-cart-items/:id} : Partial updates given fields of an existing shoppingCartItem, field will ignore if it is null
     *
     * @param id the id of the shoppingCartItemDTO to save.
     * @param shoppingCartItemDTO the shoppingCartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartItemDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shoppingCartItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ShoppingCartItemDTO>> partialUpdateShoppingCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoppingCartItemDTO shoppingCartItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ShoppingCartItem partially : {}, {}", id, shoppingCartItemDTO);
        if (shoppingCartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ShoppingCartItemDTO> result = shoppingCartItemService.partialUpdate(shoppingCartItemDTO);

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
     * {@code GET  /shopping-cart-items} : get all the Shopping Cart Items.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Shopping Cart Items in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ShoppingCartItemDTO>>> getAllShoppingCartItems(
        ShoppingCartItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ShoppingCartItems by criteria: {}", criteria);
        return shoppingCartItemService
            .countByCriteria(criteria)
            .zipWith(shoppingCartItemService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /shopping-cart-items/count} : count all the shoppingCartItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countShoppingCartItems(ShoppingCartItemCriteria criteria) {
        LOG.debug("REST request to count ShoppingCartItems by criteria: {}", criteria);
        return shoppingCartItemService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /shopping-cart-items/:id} : get the "id" shoppingCartItem.
     *
     * @param id the id of the shoppingCartItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoppingCartItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ShoppingCartItemDTO>> getShoppingCartItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ShoppingCartItem : {}", id);
        Mono<ShoppingCartItemDTO> shoppingCartItemDTO = shoppingCartItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shoppingCartItemDTO);
    }

    /**
     * {@code DELETE  /shopping-cart-items/:id} : delete the "id" shoppingCartItem.
     *
     * @param id the id of the shoppingCartItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteShoppingCartItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ShoppingCartItem : {}", id);
        return shoppingCartItemService
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
