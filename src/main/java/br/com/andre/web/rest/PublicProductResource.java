package br.com.andre.web.rest;

import br.com.andre.repository.ProductRepository;
import br.com.andre.service.dto.ProductDTO;
import br.com.andre.service.mapper.ProductMapper;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/public/products")
public class PublicProductResource {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PublicProductResource(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ProductDTO>> getActiveProducts() {
        return productRepository
            .findAll()
            .filter(product -> Boolean.TRUE.equals(product.getActive()))
            .map(productMapper::toDto)
            .collectList();
    }

    @GetMapping(value = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductDTO> getProductBySlug(@PathVariable String slug) {
        return productRepository
            .findAll()
            .filter(product -> Boolean.TRUE.equals(product.getActive()))
            .filter(product -> product.getSlug() != null)
            .filter(product -> product.getSlug().equalsIgnoreCase(slug))
            .next()
            .map(productMapper::toDto);
    }
}
