package br.com.andre.web.rest;

import static br.com.andre.domain.ShoppingCartItemAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static br.com.andre.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Product;
import br.com.andre.domain.ShoppingCart;
import br.com.andre.domain.ShoppingCartItem;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.ProductRepository;
import br.com.andre.repository.ShoppingCartItemRepository;
import br.com.andre.repository.ShoppingCartRepository;
import br.com.andre.service.ShoppingCartItemService;
import br.com.andre.service.dto.ShoppingCartItemDTO;
import br.com.andre.service.mapper.ShoppingCartItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ShoppingCartItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShoppingCartItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/shopping-cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepositoryMock;

    @Autowired
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Mock
    private ShoppingCartItemService shoppingCartItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ShoppingCartItem shoppingCartItem;

    private ShoppingCartItem insertedShoppingCartItem;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCartItem createEntity() {
        return new ShoppingCartItem().quantity(DEFAULT_QUANTITY).unitPrice(DEFAULT_UNIT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCartItem createUpdatedEntity() {
        return new ShoppingCartItem().quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ShoppingCartItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        shoppingCartItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedShoppingCartItem != null) {
            shoppingCartItemRepository.delete(insertedShoppingCartItem).block();
            insertedShoppingCartItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createShoppingCartItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);
        var returnedShoppingCartItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ShoppingCartItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ShoppingCartItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShoppingCartItem = shoppingCartItemMapper.toEntity(returnedShoppingCartItemDTO);
        assertShoppingCartItemUpdatableFieldsEquals(returnedShoppingCartItem, getPersistedShoppingCartItem(returnedShoppingCartItem));

        insertedShoppingCartItem = returnedShoppingCartItem;
    }

    @Test
    void createShoppingCartItemWithExistingId() throws Exception {
        // Create the ShoppingCartItem with an existing ID
        shoppingCartItem.setId(1L);
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shoppingCartItem.setQuantity(null);

        // Create the ShoppingCartItem, which fails.
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shoppingCartItem.setUnitPrice(null);

        // Create the ShoppingCartItem, which fails.
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllShoppingCartItems() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList
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
            .value(hasItem(shoppingCartItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoppingCartItemsWithEagerRelationshipsIsEnabled() {
        when(shoppingCartItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(shoppingCartItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoppingCartItemsWithEagerRelationshipsIsNotEnabled() {
        when(shoppingCartItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(shoppingCartItemRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getShoppingCartItem() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get the shoppingCartItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shoppingCartItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shoppingCartItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)));
    }

    @Test
    void getShoppingCartItemsByIdFiltering() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        Long id = shoppingCartItem.getId();

        defaultShoppingCartItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShoppingCartItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShoppingCartItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity equals to
        defaultShoppingCartItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsInShouldWork() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity in
        defaultShoppingCartItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsNullOrNotNull() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity is not null
        defaultShoppingCartItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity is greater than or equal to
        defaultShoppingCartItemFiltering(
            "quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY
        );
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity is less than or equal to
        defaultShoppingCartItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsLessThanSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity is less than
        defaultShoppingCartItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllShoppingCartItemsByQuantityIsGreaterThanSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where quantity is greater than
        defaultShoppingCartItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice equals to
        defaultShoppingCartItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsInShouldWork() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice in
        defaultShoppingCartItemFiltering(
            "unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE,
            "unitPrice.in=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsNullOrNotNull() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice is not null
        defaultShoppingCartItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice is greater than or equal to
        defaultShoppingCartItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice is less than or equal to
        defaultShoppingCartItemFiltering(
            "unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE
        );
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsLessThanSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice is less than
        defaultShoppingCartItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    void getAllShoppingCartItemsByUnitPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        // Get all the shoppingCartItemList where unitPrice is greater than
        defaultShoppingCartItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    void getAllShoppingCartItemsByCartIsEqualToSomething() {
        ShoppingCart cart = ShoppingCartResourceIT.createEntity();
        shoppingCartRepository.save(cart).block();
        Long cartId = cart.getId();
        shoppingCartItem.setCartId(cartId);
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();
        // Get all the shoppingCartItemList where cart equals to cartId
        defaultShoppingCartItemShouldBeFound("cartId.equals=" + cartId);

        // Get all the shoppingCartItemList where cart equals to (cartId + 1)
        defaultShoppingCartItemShouldNotBeFound("cartId.equals=" + (cartId + 1));
    }

    @Test
    void getAllShoppingCartItemsByProductIsEqualToSomething() {
        Product product = ProductResourceIT.createEntity();
        productRepository.save(product).block();
        Long productId = product.getId();
        shoppingCartItem.setProductId(productId);
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();
        // Get all the shoppingCartItemList where product equals to productId
        defaultShoppingCartItemShouldBeFound("productId.equals=" + productId);

        // Get all the shoppingCartItemList where product equals to (productId + 1)
        defaultShoppingCartItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultShoppingCartItemFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultShoppingCartItemShouldBeFound(shouldBeFound);
        defaultShoppingCartItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShoppingCartItemShouldBeFound(String filter) {
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
            .value(hasItem(shoppingCartItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)));

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
    private void defaultShoppingCartItemShouldNotBeFound(String filter) {
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
    void getNonExistingShoppingCartItem() {
        // Get the shoppingCartItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShoppingCartItem() throws Exception {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCartItem
        ShoppingCartItem updatedShoppingCartItem = shoppingCartItemRepository.findById(shoppingCartItem.getId()).block();
        updatedShoppingCartItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE);
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(updatedShoppingCartItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shoppingCartItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoppingCartItemToMatchAllProperties(updatedShoppingCartItem);
    }

    @Test
    void putNonExistingShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shoppingCartItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShoppingCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCartItem using partial update
        ShoppingCartItem partialUpdatedShoppingCartItem = new ShoppingCartItem();
        partialUpdatedShoppingCartItem.setId(shoppingCartItem.getId());

        partialUpdatedShoppingCartItem.quantity(UPDATED_QUANTITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCartItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShoppingCartItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoppingCartItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoppingCartItem, shoppingCartItem),
            getPersistedShoppingCartItem(shoppingCartItem)
        );
    }

    @Test
    void fullUpdateShoppingCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCartItem using partial update
        ShoppingCartItem partialUpdatedShoppingCartItem = new ShoppingCartItem();
        partialUpdatedShoppingCartItem.setId(shoppingCartItem.getId());

        partialUpdatedShoppingCartItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCartItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShoppingCartItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoppingCartItemUpdatableFieldsEquals(
            partialUpdatedShoppingCartItem,
            getPersistedShoppingCartItem(partialUpdatedShoppingCartItem)
        );
    }

    @Test
    void patchNonExistingShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shoppingCartItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShoppingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCartItem.setId(longCount.incrementAndGet());

        // Create the ShoppingCartItem
        ShoppingCartItemDTO shoppingCartItemDTO = shoppingCartItemMapper.toDto(shoppingCartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShoppingCartItem() {
        // Initialize the database
        insertedShoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoppingCartItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shoppingCartItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoppingCartItemRepository.count().block();
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

    protected ShoppingCartItem getPersistedShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        return shoppingCartItemRepository.findById(shoppingCartItem.getId()).block();
    }

    protected void assertPersistedShoppingCartItemToMatchAllProperties(ShoppingCartItem expectedShoppingCartItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShoppingCartItemAllPropertiesEquals(expectedShoppingCartItem, getPersistedShoppingCartItem(expectedShoppingCartItem));
        assertShoppingCartItemUpdatableFieldsEquals(expectedShoppingCartItem, getPersistedShoppingCartItem(expectedShoppingCartItem));
    }

    protected void assertPersistedShoppingCartItemToMatchUpdatableProperties(ShoppingCartItem expectedShoppingCartItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShoppingCartItemAllUpdatablePropertiesEquals(expectedShoppingCartItem, getPersistedShoppingCartItem(expectedShoppingCartItem));
        assertShoppingCartItemUpdatableFieldsEquals(expectedShoppingCartItem, getPersistedShoppingCartItem(expectedShoppingCartItem));
    }
}
