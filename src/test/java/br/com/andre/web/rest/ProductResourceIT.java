package br.com.andre.web.rest;

import static br.com.andre.domain.ProductAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static br.com.andre.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Category;
import br.com.andre.domain.Product;
import br.com.andre.repository.CategoryRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.ProductRepository;
import br.com.andre.service.ProductService;
import br.com.andre.service.dto.ProductDTO;
import br.com.andre.service.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_PROMOTIONAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PROMOTIONAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PROMOTIONAL_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_STOCK = 0;
    private static final Integer UPDATED_STOCK = 1;
    private static final Integer SMALLER_STOCK = 0 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductRepository productRepositoryMock;

    @Autowired
    private ProductMapper productMapper;

    @Mock
    private ProductService productServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Product product;

    private Product insertedProduct;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity() {
        return new Product()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .promotionalPrice(DEFAULT_PROMOTIONAL_PRICE)
            .sku(DEFAULT_SKU)
            .imageUrl(DEFAULT_IMAGE_URL)
            .stock(DEFAULT_STOCK)
            .active(DEFAULT_ACTIVE)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity() {
        return new Product()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .promotionalPrice(UPDATED_PROMOTIONAL_PRICE)
            .sku(UPDATED_SKU)
            .imageUrl(UPDATED_IMAGE_URL)
            .stock(UPDATED_STOCK)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Product.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        product = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProduct != null) {
            productRepository.delete(insertedProduct).block();
            insertedProduct = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        var returnedProductDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProductDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Product in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduct = productMapper.toEntity(returnedProductDTO);
        assertProductUpdatableFieldsEquals(returnedProduct, getPersistedProduct(returnedProduct));

        insertedProduct = returnedProduct;
    }

    @Test
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setSlug(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setPrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStockIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setStock(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setActive(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProducts() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(product.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].promotionalPrice")
            .value(hasItem(sameNumber(DEFAULT_PROMOTIONAL_PRICE)))
            .jsonPath("$.[*].sku")
            .value(hasItem(DEFAULT_SKU))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].stock")
            .value(hasItem(DEFAULT_STOCK))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsEnabled() {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductsWithEagerRelationshipsIsNotEnabled() {
        when(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(productRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProduct() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get the product
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, product.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(product.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.slug")
            .value(is(DEFAULT_SLUG))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.promotionalPrice")
            .value(is(sameNumber(DEFAULT_PROMOTIONAL_PRICE)))
            .jsonPath("$.sku")
            .value(is(DEFAULT_SKU))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.stock")
            .value(is(DEFAULT_STOCK))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updatedDate")
            .value(is(DEFAULT_UPDATED_DATE.toString()));
    }

    @Test
    void getProductsByIdFiltering() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        Long id = product.getId();

        defaultProductFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProductsByNameIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where name equals to
        defaultProductFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllProductsByNameIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where name in
        defaultProductFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllProductsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where name is not null
        defaultProductFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllProductsByNameContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where name contains
        defaultProductFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllProductsByNameNotContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where name does not contain
        defaultProductFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllProductsBySlugIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where slug equals to
        defaultProductFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    void getAllProductsBySlugIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where slug in
        defaultProductFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    void getAllProductsBySlugIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where slug is not null
        defaultProductFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    void getAllProductsBySlugContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where slug contains
        defaultProductFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    void getAllProductsBySlugNotContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where slug does not contain
        defaultProductFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    void getAllProductsByPriceIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price equals to
        defaultProductFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllProductsByPriceIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price in
        defaultProductFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllProductsByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price is not null
        defaultProductFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price is greater than or equal to
        defaultProductFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllProductsByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price is less than or equal to
        defaultProductFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllProductsByPriceIsLessThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price is less than
        defaultProductFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllProductsByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where price is greater than
        defaultProductFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllProductsByPromotionalPriceIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice equals to
        defaultProductFiltering(
            "promotionalPrice.equals=" + DEFAULT_PROMOTIONAL_PRICE,
            "promotionalPrice.equals=" + UPDATED_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsByPromotionalPriceIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice in
        defaultProductFiltering(
            "promotionalPrice.in=" + DEFAULT_PROMOTIONAL_PRICE + "," + UPDATED_PROMOTIONAL_PRICE,
            "promotionalPrice.in=" + UPDATED_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsByPromotionalPriceIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice is not null
        defaultProductFiltering("promotionalPrice.specified=true", "promotionalPrice.specified=false");
    }

    @Test
    void getAllProductsByPromotionalPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice is greater than or equal to
        defaultProductFiltering(
            "promotionalPrice.greaterThanOrEqual=" + DEFAULT_PROMOTIONAL_PRICE,
            "promotionalPrice.greaterThanOrEqual=" + UPDATED_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsByPromotionalPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice is less than or equal to
        defaultProductFiltering(
            "promotionalPrice.lessThanOrEqual=" + DEFAULT_PROMOTIONAL_PRICE,
            "promotionalPrice.lessThanOrEqual=" + SMALLER_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsByPromotionalPriceIsLessThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice is less than
        defaultProductFiltering(
            "promotionalPrice.lessThan=" + UPDATED_PROMOTIONAL_PRICE,
            "promotionalPrice.lessThan=" + DEFAULT_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsByPromotionalPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where promotionalPrice is greater than
        defaultProductFiltering(
            "promotionalPrice.greaterThan=" + SMALLER_PROMOTIONAL_PRICE,
            "promotionalPrice.greaterThan=" + DEFAULT_PROMOTIONAL_PRICE
        );
    }

    @Test
    void getAllProductsBySkuIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where sku equals to
        defaultProductFiltering("sku.equals=" + DEFAULT_SKU, "sku.equals=" + UPDATED_SKU);
    }

    @Test
    void getAllProductsBySkuIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where sku in
        defaultProductFiltering("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU, "sku.in=" + UPDATED_SKU);
    }

    @Test
    void getAllProductsBySkuIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where sku is not null
        defaultProductFiltering("sku.specified=true", "sku.specified=false");
    }

    @Test
    void getAllProductsBySkuContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where sku contains
        defaultProductFiltering("sku.contains=" + DEFAULT_SKU, "sku.contains=" + UPDATED_SKU);
    }

    @Test
    void getAllProductsBySkuNotContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where sku does not contain
        defaultProductFiltering("sku.doesNotContain=" + UPDATED_SKU, "sku.doesNotContain=" + DEFAULT_SKU);
    }

    @Test
    void getAllProductsByImageUrlIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where imageUrl equals to
        defaultProductFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProductsByImageUrlIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where imageUrl in
        defaultProductFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProductsByImageUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where imageUrl is not null
        defaultProductFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    void getAllProductsByImageUrlContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where imageUrl contains
        defaultProductFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProductsByImageUrlNotContainsSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where imageUrl does not contain
        defaultProductFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    void getAllProductsByStockIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock equals to
        defaultProductFiltering("stock.equals=" + DEFAULT_STOCK, "stock.equals=" + UPDATED_STOCK);
    }

    @Test
    void getAllProductsByStockIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock in
        defaultProductFiltering("stock.in=" + DEFAULT_STOCK + "," + UPDATED_STOCK, "stock.in=" + UPDATED_STOCK);
    }

    @Test
    void getAllProductsByStockIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock is not null
        defaultProductFiltering("stock.specified=true", "stock.specified=false");
    }

    @Test
    void getAllProductsByStockIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock is greater than or equal to
        defaultProductFiltering("stock.greaterThanOrEqual=" + DEFAULT_STOCK, "stock.greaterThanOrEqual=" + UPDATED_STOCK);
    }

    @Test
    void getAllProductsByStockIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock is less than or equal to
        defaultProductFiltering("stock.lessThanOrEqual=" + DEFAULT_STOCK, "stock.lessThanOrEqual=" + SMALLER_STOCK);
    }

    @Test
    void getAllProductsByStockIsLessThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock is less than
        defaultProductFiltering("stock.lessThan=" + UPDATED_STOCK, "stock.lessThan=" + DEFAULT_STOCK);
    }

    @Test
    void getAllProductsByStockIsGreaterThanSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where stock is greater than
        defaultProductFiltering("stock.greaterThan=" + SMALLER_STOCK, "stock.greaterThan=" + DEFAULT_STOCK);
    }

    @Test
    void getAllProductsByActiveIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where active equals to
        defaultProductFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllProductsByActiveIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where active in
        defaultProductFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllProductsByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where active is not null
        defaultProductFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    void getAllProductsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where createdDate equals to
        defaultProductFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllProductsByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where createdDate in
        defaultProductFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllProductsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where createdDate is not null
        defaultProductFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllProductsByUpdatedDateIsEqualToSomething() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where updatedDate equals to
        defaultProductFiltering("updatedDate.equals=" + DEFAULT_UPDATED_DATE, "updatedDate.equals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    void getAllProductsByUpdatedDateIsInShouldWork() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where updatedDate in
        defaultProductFiltering(
            "updatedDate.in=" + DEFAULT_UPDATED_DATE + "," + UPDATED_UPDATED_DATE,
            "updatedDate.in=" + UPDATED_UPDATED_DATE
        );
    }

    @Test
    void getAllProductsByUpdatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        // Get all the productList where updatedDate is not null
        defaultProductFiltering("updatedDate.specified=true", "updatedDate.specified=false");
    }

    @Test
    void getAllProductsByCategoryIsEqualToSomething() {
        Category category = CategoryResourceIT.createEntity();
        categoryRepository.save(category).block();
        Long categoryId = category.getId();
        product.setCategoryId(categoryId);
        insertedProduct = productRepository.save(product).block();
        // Get all the productList where category equals to categoryId
        defaultProductShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the productList where category equals to (categoryId + 1)
        defaultProductShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultProductFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProductShouldBeFound(shouldBeFound);
        defaultProductShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(product.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].slug")
            .value(hasItem(DEFAULT_SLUG))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].promotionalPrice")
            .value(hasItem(sameNumber(DEFAULT_PROMOTIONAL_PRICE)))
            .jsonPath("$.[*].sku")
            .value(hasItem(DEFAULT_SKU))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].stock")
            .value(hasItem(DEFAULT_STOCK))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingProduct() {
        // Get the product
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).block();
        updatedProduct
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .promotionalPrice(UPDATED_PROMOTIONAL_PRICE)
            .sku(UPDATED_SKU)
            .imageUrl(UPDATED_IMAGE_URL)
            .stock(UPDATED_STOCK)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);
    }

    @Test
    void putNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .promotionalPrice(UPDATED_PROMOTIONAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduct, product), getPersistedProduct(product));
    }

    @Test
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .promotionalPrice(UPDATED_PROMOTIONAL_PRICE)
            .sku(UPDATED_SKU)
            .imageUrl(UPDATED_IMAGE_URL)
            .stock(UPDATED_STOCK)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProduct))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(partialUpdatedProduct, getPersistedProduct(partialUpdatedProduct));
    }

    @Test
    void patchNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduct() {
        // Initialize the database
        insertedProduct = productRepository.save(product).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the product
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, product.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Product getPersistedProduct(Product product) {
        return productRepository.findById(product.getId()).block();
    }

    protected void assertPersistedProductToMatchAllProperties(Product expectedProduct) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductAllPropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
        assertProductUpdatableFieldsEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }

    protected void assertPersistedProductToMatchUpdatableProperties(Product expectedProduct) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductAllUpdatablePropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
        assertProductUpdatableFieldsEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }
}
