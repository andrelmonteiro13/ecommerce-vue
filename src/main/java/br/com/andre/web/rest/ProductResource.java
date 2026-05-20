package br.com.andre.web.rest;

import br.com.andre.domain.criteria.ProductCriteria;
import br.com.andre.repository.ProductRepository;
import br.com.andre.service.FileStorageService;
import br.com.andre.service.ProductService;
import br.com.andre.service.dto.ProductDTO;
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

@RestController
@RequestMapping("/api/products")
public class ProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name:ecommerce}")
    private String applicationName;

    private final ProductService productService;

    private final ProductRepository productRepository;

    private final FileStorageService fileStorageService;

    public ProductResource(ProductService productService, ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("")
    public Mono<ResponseEntity<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException {
        LOG.debug("REST request to save Product : {}", productDTO);

        if (productDTO.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }

        return productService
            .save(productDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/products/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductDTO>> updateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductDTO productDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Product : {}, {}", id, productDTO);

        if (productDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(id, productDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productService
                    .update(productDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductDTO>> partialUpdateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductDTO productDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Product partially : {}, {}", id, productDTO);

        if (productDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(id, productDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductDTO> result = productService.partialUpdate(productDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ProductDTO>> uploadImage(@PathVariable Long id, @RequestPart("file") FilePart file) {
        LOG.debug("REST request to upload image for Product : {}", id);

        return productService
            .findOne(id)
            .switchIfEmpty(Mono.error(new BadRequestAlertException("Produto não encontrado", ENTITY_NAME, "idnotfound")))
            .flatMap(productDTO ->
                fileStorageService
                    .uploadProductImage(file)
                    .flatMap(imageUrl -> {
                        productDTO.setImageUrl(imageUrl);
                        return productService.save(productDTO);
                    })
            )
            .map(ResponseEntity::ok);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProductDTO>>> getAllProducts(
        ProductCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Products by criteria: {}", criteria);

        return productService
            .countByCriteria(criteria)
            .zipWith(productService.findByCriteria(criteria, pageable).collectList())
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

    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProducts(ProductCriteria criteria) {
        LOG.debug("REST request to count Products by criteria: {}", criteria);
        return productService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDTO>> getProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Product : {}", id);
        Mono<ProductDTO> productDTO = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Product : {}", id);

        return productService
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
