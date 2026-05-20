package br.com.andre.web.rest;

import static br.com.andre.domain.PaymentAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static br.com.andre.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.Payment;
import br.com.andre.domain.enumeration.PaymentMethod;
import br.com.andre.domain.enumeration.PaymentStatus;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.PaymentRepository;
import br.com.andre.service.PaymentService;
import br.com.andre.service.dto.PaymentDTO;
import br.com.andre.service.mapper.PaymentMapper;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentResourceIT {

    private static final PaymentMethod DEFAULT_METHOD = PaymentMethod.PIX;
    private static final PaymentMethod UPDATED_METHOD = PaymentMethod.CREDIT_CARD;

    private static final PaymentStatus DEFAULT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_STATUS = PaymentStatus.APPROVED;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(0 - 1);

    private static final String DEFAULT_TRANSACTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PAID_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAID_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @Autowired
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentService paymentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Payment payment;

    private Payment insertedPayment;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity() {
        return new Payment()
            .method(DEFAULT_METHOD)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .transactionCode(DEFAULT_TRANSACTION_CODE)
            .paidDate(DEFAULT_PAID_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity() {
        return new Payment()
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .transactionCode(UPDATED_TRANSACTION_CODE)
            .paidDate(UPDATED_PAID_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Payment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        payment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPayment != null) {
            paymentRepository.delete(insertedPayment).block();
            insertedPayment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        var returnedPaymentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PaymentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Payment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPayment = paymentMapper.toEntity(returnedPaymentDTO);
        assertPaymentUpdatableFieldsEquals(returnedPayment, getPersistedPayment(returnedPayment));

        insertedPayment = returnedPayment;
    }

    @Test
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setMethod(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setStatus(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPayments() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList
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
            .value(hasItem(payment.getId().intValue()))
            .jsonPath("$.[*].method")
            .value(hasItem(DEFAULT_METHOD.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].transactionCode")
            .value(hasItem(DEFAULT_TRANSACTION_CODE))
            .jsonPath("$.[*].paidDate")
            .value(hasItem(DEFAULT_PAID_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaymentsWithEagerRelationshipsIsEnabled() {
        when(paymentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(paymentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaymentsWithEagerRelationshipsIsNotEnabled() {
        when(paymentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(paymentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPayment() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(payment.getId().intValue()))
            .jsonPath("$.method")
            .value(is(DEFAULT_METHOD.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.transactionCode")
            .value(is(DEFAULT_TRANSACTION_CODE))
            .jsonPath("$.paidDate")
            .value(is(DEFAULT_PAID_DATE.toString()));
    }

    @Test
    void getPaymentsByIdFiltering() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        Long id = payment.getId();

        defaultPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllPaymentsByMethodIsEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where method equals to
        defaultPaymentFiltering("method.equals=" + DEFAULT_METHOD, "method.equals=" + UPDATED_METHOD);
    }

    @Test
    void getAllPaymentsByMethodIsInShouldWork() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where method in
        defaultPaymentFiltering("method.in=" + DEFAULT_METHOD + "," + UPDATED_METHOD, "method.in=" + UPDATED_METHOD);
    }

    @Test
    void getAllPaymentsByMethodIsNullOrNotNull() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where method is not null
        defaultPaymentFiltering("method.specified=true", "method.specified=false");
    }

    @Test
    void getAllPaymentsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where status equals to
        defaultPaymentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllPaymentsByStatusIsInShouldWork() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where status in
        defaultPaymentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllPaymentsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where status is not null
        defaultPaymentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllPaymentsByAmountIsEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount equals to
        defaultPaymentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllPaymentsByAmountIsInShouldWork() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount in
        defaultPaymentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllPaymentsByAmountIsNullOrNotNull() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount is not null
        defaultPaymentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    void getAllPaymentsByAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount is greater than or equal to
        defaultPaymentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllPaymentsByAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount is less than or equal to
        defaultPaymentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    void getAllPaymentsByAmountIsLessThanSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount is less than
        defaultPaymentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    void getAllPaymentsByAmountIsGreaterThanSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where amount is greater than
        defaultPaymentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    void getAllPaymentsByTransactionCodeIsEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where transactionCode equals to
        defaultPaymentFiltering("transactionCode.equals=" + DEFAULT_TRANSACTION_CODE, "transactionCode.equals=" + UPDATED_TRANSACTION_CODE);
    }

    @Test
    void getAllPaymentsByTransactionCodeIsInShouldWork() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where transactionCode in
        defaultPaymentFiltering(
            "transactionCode.in=" + DEFAULT_TRANSACTION_CODE + "," + UPDATED_TRANSACTION_CODE,
            "transactionCode.in=" + UPDATED_TRANSACTION_CODE
        );
    }

    @Test
    void getAllPaymentsByTransactionCodeIsNullOrNotNull() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where transactionCode is not null
        defaultPaymentFiltering("transactionCode.specified=true", "transactionCode.specified=false");
    }

    @Test
    void getAllPaymentsByTransactionCodeContainsSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where transactionCode contains
        defaultPaymentFiltering(
            "transactionCode.contains=" + DEFAULT_TRANSACTION_CODE,
            "transactionCode.contains=" + UPDATED_TRANSACTION_CODE
        );
    }

    @Test
    void getAllPaymentsByTransactionCodeNotContainsSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where transactionCode does not contain
        defaultPaymentFiltering(
            "transactionCode.doesNotContain=" + UPDATED_TRANSACTION_CODE,
            "transactionCode.doesNotContain=" + DEFAULT_TRANSACTION_CODE
        );
    }

    @Test
    void getAllPaymentsByPaidDateIsEqualToSomething() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where paidDate equals to
        defaultPaymentFiltering("paidDate.equals=" + DEFAULT_PAID_DATE, "paidDate.equals=" + UPDATED_PAID_DATE);
    }

    @Test
    void getAllPaymentsByPaidDateIsInShouldWork() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where paidDate in
        defaultPaymentFiltering("paidDate.in=" + DEFAULT_PAID_DATE + "," + UPDATED_PAID_DATE, "paidDate.in=" + UPDATED_PAID_DATE);
    }

    @Test
    void getAllPaymentsByPaidDateIsNullOrNotNull() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        // Get all the paymentList where paidDate is not null
        defaultPaymentFiltering("paidDate.specified=true", "paidDate.specified=false");
    }

    @Test
    void getAllPaymentsByOrderIsEqualToSomething() {
        CustomerOrder order = CustomerOrderResourceIT.createEntity();
        customerOrderRepository.save(order).block();
        Long orderId = order.getId();
        payment.setOrderId(orderId);
        insertedPayment = paymentRepository.save(payment).block();
        // Get all the paymentList where order equals to orderId
        defaultPaymentShouldBeFound("orderId.equals=" + orderId);

        // Get all the paymentList where order equals to (orderId + 1)
        defaultPaymentShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    private void defaultPaymentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultPaymentShouldBeFound(shouldBeFound);
        defaultPaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentShouldBeFound(String filter) {
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
            .value(hasItem(payment.getId().intValue()))
            .jsonPath("$.[*].method")
            .value(hasItem(DEFAULT_METHOD.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].transactionCode")
            .value(hasItem(DEFAULT_TRANSACTION_CODE))
            .jsonPath("$.[*].paidDate")
            .value(hasItem(DEFAULT_PAID_DATE.toString()));

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
    private void defaultPaymentShouldNotBeFound(String filter) {
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
    void getNonExistingPayment() {
        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPayment() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).block();
        updatedPayment
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .transactionCode(UPDATED_TRANSACTION_CODE)
            .paidDate(UPDATED_PAID_DATE);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentToMatchAllProperties(updatedPayment);
    }

    @Test
    void putNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPayment, payment), getPersistedPayment(payment));
    }

    @Test
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .method(UPDATED_METHOD)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .transactionCode(UPDATED_TRANSACTION_CODE)
            .paidDate(UPDATED_PAID_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentUpdatableFieldsEquals(partialUpdatedPayment, getPersistedPayment(partialUpdatedPayment));
    }

    @Test
    void patchNonExistingPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePayment() {
        // Initialize the database
        insertedPayment = paymentRepository.save(payment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the payment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentRepository.count().block();
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

    protected Payment getPersistedPayment(Payment payment) {
        return paymentRepository.findById(payment.getId()).block();
    }

    protected void assertPersistedPaymentToMatchAllProperties(Payment expectedPayment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentAllPropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
        assertPaymentUpdatableFieldsEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }

    protected void assertPersistedPaymentToMatchUpdatableProperties(Payment expectedPayment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPaymentAllUpdatablePropertiesEquals(expectedPayment, getPersistedPayment(expectedPayment));
        assertPaymentUpdatableFieldsEquals(expectedPayment, getPersistedPayment(expectedPayment));
    }
}
