package br.com.andre.web.rest;

import static br.com.andre.domain.CustomerAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Customer;
import br.com.andre.repository.CustomerRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.service.dto.CustomerDTO;
import br.com.andre.service.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENT = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Customer customer;

    private Customer insertedCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        return new Customer()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .document(DEFAULT_DOCUMENT)
            .createdDate(DEFAULT_CREATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity() {
        return new Customer()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .document(UPDATED_DOCUMENT)
            .createdDate(UPDATED_CREATED_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Customer.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        customer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCustomer != null) {
            customerRepository.delete(insertedCustomer).block();
            insertedCustomer = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCustomer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);
        var returnedCustomerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CustomerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Customer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomer = customerMapper.toEntity(returnedCustomerDTO);
        assertCustomerUpdatableFieldsEquals(returnedCustomer, getPersistedCustomer(returnedCustomer));

        insertedCustomer = returnedCustomer;
    }

    @Test
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId(1L);
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setFirstName(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setLastName(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setEmail(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomers() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList
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
            .value(hasItem(customer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].document")
            .value(hasItem(DEFAULT_DOCUMENT))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    void getCustomer() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get the customer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customer.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.document")
            .value(is(DEFAULT_DOCUMENT))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    void getCustomersByIdFiltering() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        Long id = customer.getId();

        defaultCustomerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCustomersByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where firstName equals to
        defaultCustomerFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCustomersByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where firstName in
        defaultCustomerFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCustomersByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where firstName is not null
        defaultCustomerFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllCustomersByFirstNameContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where firstName contains
        defaultCustomerFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCustomersByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where firstName does not contain
        defaultCustomerFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllCustomersByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where lastName equals to
        defaultCustomerFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCustomersByLastNameIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where lastName in
        defaultCustomerFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCustomersByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where lastName is not null
        defaultCustomerFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllCustomersByLastNameContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where lastName contains
        defaultCustomerFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCustomersByLastNameNotContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where lastName does not contain
        defaultCustomerFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllCustomersByEmailIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where email equals to
        defaultCustomerFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCustomersByEmailIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where email in
        defaultCustomerFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCustomersByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where email is not null
        defaultCustomerFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllCustomersByEmailContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where email contains
        defaultCustomerFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCustomersByEmailNotContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where email does not contain
        defaultCustomerFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllCustomersByPhoneIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where phone equals to
        defaultCustomerFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    void getAllCustomersByPhoneIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where phone in
        defaultCustomerFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    void getAllCustomersByPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where phone is not null
        defaultCustomerFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    void getAllCustomersByPhoneContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where phone contains
        defaultCustomerFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    void getAllCustomersByPhoneNotContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where phone does not contain
        defaultCustomerFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    void getAllCustomersByDocumentIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where document equals to
        defaultCustomerFiltering("document.equals=" + DEFAULT_DOCUMENT, "document.equals=" + UPDATED_DOCUMENT);
    }

    @Test
    void getAllCustomersByDocumentIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where document in
        defaultCustomerFiltering("document.in=" + DEFAULT_DOCUMENT + "," + UPDATED_DOCUMENT, "document.in=" + UPDATED_DOCUMENT);
    }

    @Test
    void getAllCustomersByDocumentIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where document is not null
        defaultCustomerFiltering("document.specified=true", "document.specified=false");
    }

    @Test
    void getAllCustomersByDocumentContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where document contains
        defaultCustomerFiltering("document.contains=" + DEFAULT_DOCUMENT, "document.contains=" + UPDATED_DOCUMENT);
    }

    @Test
    void getAllCustomersByDocumentNotContainsSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where document does not contain
        defaultCustomerFiltering("document.doesNotContain=" + UPDATED_DOCUMENT, "document.doesNotContain=" + DEFAULT_DOCUMENT);
    }

    @Test
    void getAllCustomersByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where createdDate equals to
        defaultCustomerFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllCustomersByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where createdDate in
        defaultCustomerFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllCustomersByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList where createdDate is not null
        defaultCustomerFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    private void defaultCustomerFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCustomerShouldBeFound(shouldBeFound);
        defaultCustomerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerShouldBeFound(String filter) {
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
            .value(hasItem(customer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].document")
            .value(hasItem(DEFAULT_DOCUMENT))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()));

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
    private void defaultCustomerShouldNotBeFound(String filter) {
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
    void getNonExistingCustomer() {
        // Get the customer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).block();
        updatedCustomer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .document(UPDATED_DOCUMENT)
            .createdDate(UPDATED_CREATED_DATE);
        CustomerDTO customerDTO = customerMapper.toDto(updatedCustomer);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerToMatchAllProperties(updatedCustomer);
    }

    @Test
    void putNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).createdDate(UPDATED_CREATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCustomer, customer), getPersistedCustomer(customer));
    }

    @Test
    void fullUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .document(UPDATED_DOCUMENT)
            .createdDate(UPDATED_CREATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(partialUpdatedCustomer, getPersistedCustomer(partialUpdatedCustomer));
    }

    @Test
    void patchNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomer() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerRepository.count().block();
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

    protected Customer getPersistedCustomer(Customer customer) {
        return customerRepository.findById(customer.getId()).block();
    }

    protected void assertPersistedCustomerToMatchAllProperties(Customer expectedCustomer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerAllPropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
        assertCustomerUpdatableFieldsEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }

    protected void assertPersistedCustomerToMatchUpdatableProperties(Customer expectedCustomer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCustomerAllUpdatablePropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
        assertCustomerUpdatableFieldsEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }
}
