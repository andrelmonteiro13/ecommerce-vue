package br.com.andre.web.rest;

import static br.com.andre.domain.CustomerOrderAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static br.com.andre.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Address;
import br.com.andre.domain.Customer;
import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.User;
import br.com.andre.domain.enumeration.CustomerOrderStatus;
import br.com.andre.repository.AddressRepository;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.repository.CustomerRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.UserRepository;
import br.com.andre.repository.UserRepository;
import br.com.andre.service.CustomerOrderService;
import br.com.andre.service.dto.CustomerOrderDTO;
import br.com.andre.service.mapper.CustomerOrderMapper;
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
 * Integration tests for the {@link CustomerOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerOrderResourceIT {

    private static final String DEFAULT_ORDER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final CustomerOrderStatus DEFAULT_STATUS = CustomerOrderStatus.CREATED;
    private static final CustomerOrderStatus UPDATED_STATUS = CustomerOrderStatus.WAITING_PAYMENT;

    private static final BigDecimal DEFAULT_SUBTOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_SUBTOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_SUBTOTAL = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_SHIPPING_COST = new BigDecimal(0);
    private static final BigDecimal UPDATED_SHIPPING_COST = new BigDecimal(1);
    private static final BigDecimal SMALLER_SHIPPING_COST = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customer-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CustomerOrderRepository customerOrderRepositoryMock;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Mock
    private CustomerOrderService customerOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CustomerOrder customerOrder;

    private CustomerOrder insertedCustomerOrder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createEntity() {
        return new CustomerOrder()
            .orderNumber(DEFAULT_ORDER_NUMBER)
            .orderDate(DEFAULT_ORDER_DATE)
            .status(DEFAULT_STATUS)
            .subtotal(DEFAULT_SUBTOTAL)
            .discount(DEFAULT_DISCOUNT)
            .shippingCost(DEFAULT_SHIPPING_COST)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createUpdatedEntity() {
        return new CustomerOrder()
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderDate(UPDATED_ORDER_DATE)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discount(UPDATED_DISCOUNT)
            .shippingCost(UPDATED_SHIPPING_COST)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .notes(UPDATED_NOTES);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CustomerOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        customerOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomerOrder != null) {
            customerOrderRepository.delete(insertedCustomerOrder).block();
            insertedCustomerOrder = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createCustomerOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);
        var returnedCustomerOrderDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CustomerOrderDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the CustomerOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomerOrder = customerOrderMapper.toEntity(returnedCustomerOrderDTO);
        assertCustomerOrderUpdatableFieldsEquals(returnedCustomerOrder, getPersistedCustomerOrder(returnedCustomerOrder));

        insertedCustomerOrder = returnedCustomerOrder;
    }

    @Test
    void createCustomerOrderWithExistingId() throws Exception {
        // Create the CustomerOrder with an existing ID
        customerOrder.setId(1L);
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkOrderNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setOrderNumber(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setOrderDate(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setStatus(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSubtotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setSubtotal(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setTotalPrice(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomerOrders() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList
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
            .value(hasItem(customerOrder.getId().intValue()))
            .jsonPath("$.[*].orderNumber")
            .value(hasItem(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].discount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT)))
            .jsonPath("$.[*].shippingCost")
            .value(hasItem(sameNumber(DEFAULT_SHIPPING_COST)))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrdersWithEagerRelationshipsIsEnabled() {
        when(customerOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(customerOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrdersWithEagerRelationshipsIsNotEnabled() {
        when(customerOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(customerOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCustomerOrder() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get the customerOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customerOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customerOrder.getId().intValue()))
            .jsonPath("$.orderNumber")
            .value(is(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.orderDate")
            .value(is(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.subtotal")
            .value(is(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.discount")
            .value(is(sameNumber(DEFAULT_DISCOUNT)))
            .jsonPath("$.shippingCost")
            .value(is(sameNumber(DEFAULT_SHIPPING_COST)))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getCustomerOrdersByIdFiltering() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        Long id = customerOrder.getId();

        defaultCustomerOrderFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCustomerOrdersByOrderNumberIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderNumber equals to
        defaultCustomerOrderFiltering("orderNumber.equals=" + DEFAULT_ORDER_NUMBER, "orderNumber.equals=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    void getAllCustomerOrdersByOrderNumberIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderNumber in
        defaultCustomerOrderFiltering(
            "orderNumber.in=" + DEFAULT_ORDER_NUMBER + "," + UPDATED_ORDER_NUMBER,
            "orderNumber.in=" + UPDATED_ORDER_NUMBER
        );
    }

    @Test
    void getAllCustomerOrdersByOrderNumberIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderNumber is not null
        defaultCustomerOrderFiltering("orderNumber.specified=true", "orderNumber.specified=false");
    }

    @Test
    void getAllCustomerOrdersByOrderNumberContainsSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderNumber contains
        defaultCustomerOrderFiltering("orderNumber.contains=" + DEFAULT_ORDER_NUMBER, "orderNumber.contains=" + UPDATED_ORDER_NUMBER);
    }

    @Test
    void getAllCustomerOrdersByOrderNumberNotContainsSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderNumber does not contain
        defaultCustomerOrderFiltering(
            "orderNumber.doesNotContain=" + UPDATED_ORDER_NUMBER,
            "orderNumber.doesNotContain=" + DEFAULT_ORDER_NUMBER
        );
    }

    @Test
    void getAllCustomerOrdersByOrderDateIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderDate equals to
        defaultCustomerOrderFiltering("orderDate.equals=" + DEFAULT_ORDER_DATE, "orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    void getAllCustomerOrdersByOrderDateIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderDate in
        defaultCustomerOrderFiltering(
            "orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE,
            "orderDate.in=" + UPDATED_ORDER_DATE
        );
    }

    @Test
    void getAllCustomerOrdersByOrderDateIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where orderDate is not null
        defaultCustomerOrderFiltering("orderDate.specified=true", "orderDate.specified=false");
    }

    @Test
    void getAllCustomerOrdersByStatusIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where status equals to
        defaultCustomerOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllCustomerOrdersByStatusIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where status in
        defaultCustomerOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllCustomerOrdersByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where status is not null
        defaultCustomerOrderFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal equals to
        defaultCustomerOrderFiltering("subtotal.equals=" + DEFAULT_SUBTOTAL, "subtotal.equals=" + UPDATED_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal in
        defaultCustomerOrderFiltering("subtotal.in=" + DEFAULT_SUBTOTAL + "," + UPDATED_SUBTOTAL, "subtotal.in=" + UPDATED_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal is not null
        defaultCustomerOrderFiltering("subtotal.specified=true", "subtotal.specified=false");
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal is greater than or equal to
        defaultCustomerOrderFiltering("subtotal.greaterThanOrEqual=" + DEFAULT_SUBTOTAL, "subtotal.greaterThanOrEqual=" + UPDATED_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal is less than or equal to
        defaultCustomerOrderFiltering("subtotal.lessThanOrEqual=" + DEFAULT_SUBTOTAL, "subtotal.lessThanOrEqual=" + SMALLER_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal is less than
        defaultCustomerOrderFiltering("subtotal.lessThan=" + UPDATED_SUBTOTAL, "subtotal.lessThan=" + DEFAULT_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersBySubtotalIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where subtotal is greater than
        defaultCustomerOrderFiltering("subtotal.greaterThan=" + SMALLER_SUBTOTAL, "subtotal.greaterThan=" + DEFAULT_SUBTOTAL);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount equals to
        defaultCustomerOrderFiltering("discount.equals=" + DEFAULT_DISCOUNT, "discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount in
        defaultCustomerOrderFiltering("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT, "discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount is not null
        defaultCustomerOrderFiltering("discount.specified=true", "discount.specified=false");
    }

    @Test
    void getAllCustomerOrdersByDiscountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount is greater than or equal to
        defaultCustomerOrderFiltering("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT, "discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount is less than or equal to
        defaultCustomerOrderFiltering("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT, "discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount is less than
        defaultCustomerOrderFiltering("discount.lessThan=" + UPDATED_DISCOUNT, "discount.lessThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByDiscountIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where discount is greater than
        defaultCustomerOrderFiltering("discount.greaterThan=" + SMALLER_DISCOUNT, "discount.greaterThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost equals to
        defaultCustomerOrderFiltering("shippingCost.equals=" + DEFAULT_SHIPPING_COST, "shippingCost.equals=" + UPDATED_SHIPPING_COST);
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost in
        defaultCustomerOrderFiltering(
            "shippingCost.in=" + DEFAULT_SHIPPING_COST + "," + UPDATED_SHIPPING_COST,
            "shippingCost.in=" + UPDATED_SHIPPING_COST
        );
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost is not null
        defaultCustomerOrderFiltering("shippingCost.specified=true", "shippingCost.specified=false");
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost is greater than or equal to
        defaultCustomerOrderFiltering(
            "shippingCost.greaterThanOrEqual=" + DEFAULT_SHIPPING_COST,
            "shippingCost.greaterThanOrEqual=" + UPDATED_SHIPPING_COST
        );
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost is less than or equal to
        defaultCustomerOrderFiltering(
            "shippingCost.lessThanOrEqual=" + DEFAULT_SHIPPING_COST,
            "shippingCost.lessThanOrEqual=" + SMALLER_SHIPPING_COST
        );
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost is less than
        defaultCustomerOrderFiltering("shippingCost.lessThan=" + UPDATED_SHIPPING_COST, "shippingCost.lessThan=" + DEFAULT_SHIPPING_COST);
    }

    @Test
    void getAllCustomerOrdersByShippingCostIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where shippingCost is greater than
        defaultCustomerOrderFiltering(
            "shippingCost.greaterThan=" + SMALLER_SHIPPING_COST,
            "shippingCost.greaterThan=" + DEFAULT_SHIPPING_COST
        );
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice equals to
        defaultCustomerOrderFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice in
        defaultCustomerOrderFiltering(
            "totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE,
            "totalPrice.in=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice is not null
        defaultCustomerOrderFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice is greater than or equal to
        defaultCustomerOrderFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice is less than or equal to
        defaultCustomerOrderFiltering(
            "totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE
        );
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsLessThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice is less than
        defaultCustomerOrderFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrdersByTotalPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where totalPrice is greater than
        defaultCustomerOrderFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllCustomerOrdersByNotesIsEqualToSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where notes equals to
        defaultCustomerOrderFiltering("notes.equals=" + DEFAULT_NOTES, "notes.equals=" + UPDATED_NOTES);
    }

    @Test
    void getAllCustomerOrdersByNotesIsInShouldWork() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where notes in
        defaultCustomerOrderFiltering("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES, "notes.in=" + UPDATED_NOTES);
    }

    @Test
    void getAllCustomerOrdersByNotesIsNullOrNotNull() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where notes is not null
        defaultCustomerOrderFiltering("notes.specified=true", "notes.specified=false");
    }

    @Test
    void getAllCustomerOrdersByNotesContainsSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where notes contains
        defaultCustomerOrderFiltering("notes.contains=" + DEFAULT_NOTES, "notes.contains=" + UPDATED_NOTES);
    }

    @Test
    void getAllCustomerOrdersByNotesNotContainsSomething() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        // Get all the customerOrderList where notes does not contain
        defaultCustomerOrderFiltering("notes.doesNotContain=" + UPDATED_NOTES, "notes.doesNotContain=" + DEFAULT_NOTES);
    }

    @Test
    void getAllCustomerOrdersByCustomerIsEqualToSomething() {
        Customer customer = CustomerResourceIT.createEntity();
        customerRepository.save(customer).block();
        Long customerId = customer.getId();
        customerOrder.setCustomerId(customerId);
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();
        // Get all the customerOrderList where customer equals to customerId
        defaultCustomerOrderShouldBeFound("customerId.equals=" + customerId);

        // Get all the customerOrderList where customer equals to (customerId + 1)
        defaultCustomerOrderShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    void getAllCustomerOrdersByUserIsEqualToSomething() {
        User user = UserResourceIT.createEntity();
        userRepository.save(user).block();
        Long userId = user.getId();
        customerOrder.setUserId(userId);
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();
        // Get all the customerOrderList where user equals to userId
        defaultCustomerOrderShouldBeFound("userId.equals=" + userId);

        // Get all the customerOrderList where user equals to (userId + 1)
        defaultCustomerOrderShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    void getAllCustomerOrdersByShippingAddressIsEqualToSomething() {
        Address shippingAddress = AddressResourceIT.createEntity();
        addressRepository.save(shippingAddress).block();
        Long shippingAddressId = shippingAddress.getId();
        customerOrder.setShippingAddressId(shippingAddressId);
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();
        // Get all the customerOrderList where shippingAddress equals to shippingAddressId
        defaultCustomerOrderShouldBeFound("shippingAddressId.equals=" + shippingAddressId);

        // Get all the customerOrderList where shippingAddress equals to (shippingAddressId + 1)
        defaultCustomerOrderShouldNotBeFound("shippingAddressId.equals=" + (shippingAddressId + 1));
    }

    private void defaultCustomerOrderFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCustomerOrderShouldBeFound(shouldBeFound);
        defaultCustomerOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerOrderShouldBeFound(String filter) {
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
            .value(hasItem(customerOrder.getId().intValue()))
            .jsonPath("$.[*].orderNumber")
            .value(hasItem(DEFAULT_ORDER_NUMBER))
            .jsonPath("$.[*].orderDate")
            .value(hasItem(DEFAULT_ORDER_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].subtotal")
            .value(hasItem(sameNumber(DEFAULT_SUBTOTAL)))
            .jsonPath("$.[*].discount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT)))
            .jsonPath("$.[*].shippingCost")
            .value(hasItem(sameNumber(DEFAULT_SHIPPING_COST)))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));

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
    private void defaultCustomerOrderShouldNotBeFound(String filter) {
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
    void getNonExistingCustomerOrder() {
        // Get the customerOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomerOrder() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder
        CustomerOrder updatedCustomerOrder = customerOrderRepository.findById(customerOrder.getId()).block();
        updatedCustomerOrder
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderDate(UPDATED_ORDER_DATE)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discount(UPDATED_DISCOUNT)
            .shippingCost(UPDATED_SHIPPING_COST)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .notes(UPDATED_NOTES);
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(updatedCustomerOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerOrderToMatchAllProperties(updatedCustomerOrder);
    }

    @Test
    void putNonExistingCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder using partial update
        CustomerOrder partialUpdatedCustomerOrder = new CustomerOrder();
        partialUpdatedCustomerOrder.setId(customerOrder.getId());

        partialUpdatedCustomerOrder
            .orderDate(UPDATED_ORDER_DATE)
            .subtotal(UPDATED_SUBTOTAL)
            .discount(UPDATED_DISCOUNT)
            .shippingCost(UPDATED_SHIPPING_COST)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomerOrder, customerOrder),
            getPersistedCustomerOrder(customerOrder)
        );
    }

    @Test
    void fullUpdateCustomerOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder using partial update
        CustomerOrder partialUpdatedCustomerOrder = new CustomerOrder();
        partialUpdatedCustomerOrder.setId(customerOrder.getId());

        partialUpdatedCustomerOrder
            .orderNumber(UPDATED_ORDER_NUMBER)
            .orderDate(UPDATED_ORDER_DATE)
            .status(UPDATED_STATUS)
            .subtotal(UPDATED_SUBTOTAL)
            .discount(UPDATED_DISCOUNT)
            .shippingCost(UPDATED_SHIPPING_COST)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomerOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderUpdatableFieldsEquals(partialUpdatedCustomerOrder, getPersistedCustomerOrder(partialUpdatedCustomerOrder));
    }

    @Test
    void patchNonExistingCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomerOrder() {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.save(customerOrder).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customerOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerOrderRepository.count().block();
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

    protected CustomerOrder getPersistedCustomerOrder(CustomerOrder customerOrder) {
        return customerOrderRepository.findById(customerOrder.getId()).block();
    }

    protected void assertPersistedCustomerOrderToMatchAllProperties(CustomerOrder expectedCustomerOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerOrderAllPropertiesEquals(expectedCustomerOrder, getPersistedCustomerOrder(expectedCustomerOrder));
        assertCustomerOrderUpdatableFieldsEquals(expectedCustomerOrder, getPersistedCustomerOrder(expectedCustomerOrder));
    }

    protected void assertPersistedCustomerOrderToMatchUpdatableProperties(CustomerOrder expectedCustomerOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerOrderAllUpdatablePropertiesEquals(expectedCustomerOrder, getPersistedCustomerOrder(expectedCustomerOrder));
        assertCustomerOrderUpdatableFieldsEquals(expectedCustomerOrder, getPersistedCustomerOrder(expectedCustomerOrder));
    }
}
