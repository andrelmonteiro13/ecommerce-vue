package br.com.andre.web.rest;

import static br.com.andre.domain.CustomerOrderItemAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static br.com.andre.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.CustomerOrderItem;
import br.com.andre.domain.Product;
import br.com.andre.repository.CustomerOrderItemRepository;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.ProductRepository;
import br.com.andre.service.CustomerOrderItemService;
import br.com.andre.service.dto.CustomerOrderItemDTO;
import br.com.andre.service.mapper.CustomerOrderItemMapper;
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
 * Integration tests for the {@link CustomerOrderItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerOrderItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/customer-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerOrderItemRepository customerOrderItemRepository;

    @Mock
    private CustomerOrderItemRepository customerOrderItemRepositoryMock;

    @Autowired
    private CustomerOrderItemMapper customerOrderItemMapper;

    @Mock
    private CustomerOrderItemService customerOrderItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CustomerOrderItem customerOrderItem;

    private CustomerOrderItem insertedCustomerOrderItem;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrderItem createEntity() {
        return new CustomerOrderItem().quantity(DEFAULT_QUANTITY).unitPrice(DEFAULT_UNIT_PRICE).totalPrice(DEFAULT_TOTAL_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrderItem createUpdatedEntity() {
        return new CustomerOrderItem().quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CustomerOrderItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        customerOrderItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomerOrderItem != null) {
            customerOrderItemRepository.delete(insertedCustomerOrderItem).block();
            insertedCustomerOrderItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCustomerOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);
        var returnedCustomerOrderItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CustomerOrderItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the CustomerOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomerOrderItem = customerOrderItemMapper.toEntity(returnedCustomerOrderItemDTO);
        assertCustomerOrderItemUpdatableFieldsEquals(returnedCustomerOrderItem, getPersistedCustomerOrderItem(returnedCustomerOrderItem));

        insertedCustomerOrderItem = returnedCustomerOrderItem;
    }

    @Test
    void createCustomerOrderItemWithExistingId() throws Exception {
        // Create the CustomerOrderItem with an existing ID
        customerOrderItem.setId(1L);
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrderItem.setQuantity(null);

        // Create the CustomerOrderItem, which fails.
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrderItem.setUnitPrice(null);

        // Create the CustomerOrderItem, which fails.
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrderItem.setTotalPrice(null);

        // Create the CustomerOrderItem, which fails.
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomerOrderItems() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList
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
            .value(hasItem(customerOrderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrderItemsWithEagerRelationshipsIsEnabled() {
        when(customerOrderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(customerOrderItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrderItemsWithEagerRelationshipsIsNotEnabled() {
        when(customerOrderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(customerOrderItemRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCustomerOrderItem() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get the customerOrderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customerOrderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customerOrderItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getCustomerOrderItemsByIdFiltering() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        Long id = customerOrderItem.getId();

        defaultCustomerOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerOrderItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerOrderItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity equals to
        defaultCustomerOrderItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity in
        defaultCustomerOrderItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity is not null
        defaultCustomerOrderItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity is greater than or equal to
        defaultCustomerOrderItemFiltering(
            "quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY
        );
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity is less than or equal to
        defaultCustomerOrderItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity is less than
        defaultCustomerOrderItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllCustomerOrderItemsByQuantityIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where quantity is greater than
        defaultCustomerOrderItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice equals to
        defaultCustomerOrderItemFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice in
        defaultCustomerOrderItemFiltering(
            "unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE,
            "unitPrice.in=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice is not null
        defaultCustomerOrderItemFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice is greater than or equal to
        defaultCustomerOrderItemFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice is less than or equal to
        defaultCustomerOrderItemFiltering(
            "unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice is less than
        defaultCustomerOrderItemFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByUnitPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where unitPrice is greater than
        defaultCustomerOrderItemFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice equals to
        defaultCustomerOrderItemFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice in
        defaultCustomerOrderItemFiltering(
            "totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE,
            "totalPrice.in=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice is not null
        defaultCustomerOrderItemFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice is greater than or equal to
        defaultCustomerOrderItemFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice is less than or equal to
        defaultCustomerOrderItemFiltering(
            "totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice is less than
        defaultCustomerOrderItemFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByTotalPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        // Get all the customerOrderItemList where totalPrice is greater than
        defaultCustomerOrderItemFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrderItemsByOrderIsEqualToSomething() {
        CustomerOrder order = CustomerOrderResourceIT.createEntity();
        customerOrderRepository.save(order).block();
        Long orderId = order.getId();
        customerOrderItem.setOrderId(orderId);
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();
        // Get all the customerOrderItemList where order equals to orderId
        defaultCustomerOrderItemShouldBeFound("orderId.equals=" + orderId);

        // Get all the customerOrderItemList where order equals to (orderId + 1)
        defaultCustomerOrderItemShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    void getAllCustomerOrderItemsByProductIsEqualToSomething() {
        Product product = ProductResourceIT.createEntity();
        productRepository.save(product).block();
        Long productId = product.getId();
        customerOrderItem.setProductId(productId);
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();
        // Get all the customerOrderItemList where product equals to productId
        defaultCustomerOrderItemShouldBeFound("productId.equals=" + productId);

        // Get all the customerOrderItemList where product equals to (productId + 1)
        defaultCustomerOrderItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultCustomerOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCustomerOrderItemShouldBeFound(shouldBeFound);
        defaultCustomerOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerOrderItemShouldBeFound(String filter) {
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
            .value(hasItem(customerOrderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));

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
    private void defaultCustomerOrderItemShouldNotBeFound(String filter) {
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
    void getNonExistingCustomerOrderItem() {
        // Get the customerOrderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomerOrderItem() throws Exception {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrderItem
        CustomerOrderItem updatedCustomerOrderItem = customerOrderItemRepository.findById(customerOrderItem.getId()).block();
        updatedCustomerOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(updatedCustomerOrderItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerOrderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerOrderItemToMatchAllProperties(updatedCustomerOrderItem);
    }

    @Test
    void putNonExistingCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerOrderItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrderItem using partial update
        CustomerOrderItem partialUpdatedCustomerOrderItem = new CustomerOrderItem();
        partialUpdatedCustomerOrderItem.setId(customerOrderItem.getId());

        partialUpdatedCustomerOrderItem.quantity(UPDATED_QUANTITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerOrderItem, customerOrderItem),
            getPersistedCustomerOrderItem(customerOrderItem)
        );
    }

    @Test
    void fullUpdateCustomerOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrderItem using partial update
        CustomerOrderItem partialUpdatedCustomerOrderItem = new CustomerOrderItem();
        partialUpdatedCustomerOrderItem.setId(customerOrderItem.getId());

        partialUpdatedCustomerOrderItem.quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderItemUpdatableFieldsEquals(
            partialUpdatedCustomerOrderItem,
            getPersistedCustomerOrderItem(partialUpdatedCustomerOrderItem)
        );
    }

    @Test
    void patchNonExistingCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerOrderItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomerOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrderItem.setId(longCount.incrementAndGet());

        // Create the CustomerOrderItem
        CustomerOrderItemDTO customerOrderItemDTO = customerOrderItemMapper.toDto(customerOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomerOrderItem() {
        // Initialize the database
        insertedCustomerOrderItem = customerOrderItemRepository.save(customerOrderItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerOrderItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customerOrderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerOrderItemRepository.count().block();
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

    protected CustomerOrderItem getPersistedCustomerOrderItem(CustomerOrderItem customerOrderItem) {
        return customerOrderItemRepository.findById(customerOrderItem.getId()).block();
    }

    protected void assertPersistedCustomerOrderItemToMatchAllProperties(CustomerOrderItem expectedCustomerOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerOrderItemAllPropertiesEquals(expectedCustomerOrderItem, getPersistedCustomerOrderItem(expectedCustomerOrderItem));
        assertCustomerOrderItemUpdatableFieldsEquals(expectedCustomerOrderItem, getPersistedCustomerOrderItem(expectedCustomerOrderItem));
    }

    protected void assertPersistedCustomerOrderItemToMatchUpdatableProperties(CustomerOrderItem expectedCustomerOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerOrderItemAllUpdatablePropertiesEquals(expectedCustomerOrderItem, getPersistedCustomerOrderItem(expectedCustomerOrderItem));
        assertCustomerOrderItemUpdatableFieldsEquals(expectedCustomerOrderItem, getPersistedCustomerOrderItem(expectedCustomerOrderItem));
    }
}
