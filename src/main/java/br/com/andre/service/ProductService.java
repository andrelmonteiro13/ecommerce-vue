package br.com.andre.service;

import br.com.andre.domain.criteria.ProductCriteria;
import br.com.andre.repository.ProductRepository;
import br.com.andre.service.dto.ProductDTO;
import br.com.andre.service.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link br.com.andre.domain.Product}.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductDTO> save(ProductDTO productDTO) {
        LOG.debug("Request to save Product : {}", productDTO);
        return productRepository.save(productMapper.toEntity(productDTO)).map(productMapper::toDto);
    }

    /**
     * Update a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductDTO> update(ProductDTO productDTO) {
        LOG.debug("Request to update Product : {}", productDTO);
        return productRepository.save(productMapper.toEntity(productDTO)).map(productMapper::toDto);
    }

    /**
     * Partially update a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductDTO> partialUpdate(ProductDTO productDTO) {
        LOG.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                productMapper.partialUpdate(existingProduct, productDTO);

                return existingProduct;
            })
            .flatMap(productRepository::save)
            .map(productMapper::toDto);
    }

    /**
     * Find products by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductDTO> findByCriteria(ProductCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Products by Criteria");
        return productRepository.findByCriteria(criteria, pageable).map(productMapper::toDto);
    }

    /**
     * Find the count of products by criteria.
     * @param criteria filtering criteria
     * @return the count of products
     */
    public Mono<Long> countByCriteria(ProductCriteria criteria) {
        LOG.debug("Request to get the count of all Products by Criteria");
        return productRepository.countByCriteria(criteria);
    }

    /**
     * Get all the products with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<ProductDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productRepository.findAllWithEagerRelationships(pageable).map(productMapper::toDto);
    }

    /**
     * Returns the number of products available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productRepository.count();
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductDTO> findOne(Long id) {
        LOG.debug("Request to get Product : {}", id);
        return productRepository.findOneWithEagerRelationships(id).map(productMapper::toDto);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Product : {}", id);
        return productRepository.deleteById(id);
    }
}
