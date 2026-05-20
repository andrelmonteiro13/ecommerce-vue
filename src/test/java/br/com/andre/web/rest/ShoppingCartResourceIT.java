package br.com.andre.web.rest;

import static br.com.andre.domain.ShoppingCartAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Customer;
import br.com.andre.domain.ShoppingCart;
import br.com.andre.repository.CustomerRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.ShoppingCartRepository;
import br.com.andre.service.ShoppingCartService;
import br.com.andre.service.dto.ShoppingCartDTO;
import br.com.andre.service.mapper.ShoppingCartMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link ShoppingCartResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShoppingCartResourceIT {

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/shopping-carts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepositoryMock;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private ShoppingCartService shoppingCartServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ShoppingCart shoppingCart;

    private ShoppingCart insertedShoppingCart;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCart createEntity() {
        return new ShoppingCart().createdDate(DEFAULT_CREATED_DATE).updatedDate(DEFAULT_UPDATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCart createUpdatedEntity() {
        return new ShoppingCart().createdDate(UPDATED_CREATED_DATE).updatedDate(UPDATED_UPDATED_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ShoppingCart.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        shoppingCart = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedShoppingCart != null) {
            shoppingCartRepository.delete(insertedShoppingCart).block();
            insertedShoppingCart = null;
        }
        deleteEntities(em);
    }

    @Test
    void createShoppingCart() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);
        var returnedShoppingCartDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ShoppingCartDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ShoppingCart in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShoppingCart = shoppingCartMapper.toEntity(returnedShoppingCartDTO);
        assertShoppingCartUpdatableFieldsEquals(returnedShoppingCart, getPersistedShoppingCart(returnedShoppingCart));

        insertedShoppingCart = returnedShoppingCart;
    }

    @Test
    void createShoppingCartWithExistingId() throws Exception {
        // Create the ShoppingCart with an existing ID
        shoppingCart.setId(1L);
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shoppingCart.setCreatedDate(null);

        // Create the ShoppingCart, which fails.
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllShoppingCarts() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList
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
            .value(hasItem(shoppingCart.getId().intValue()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].updatedDate")
            .value(hasItem(DEFAULT_UPDATED_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoppingCartsWithEagerRelationshipsIsEnabled() {
        when(shoppingCartServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(shoppingCartServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShoppingCartsWithEagerRelationshipsIsNotEnabled() {
        when(shoppingCartServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(shoppingCartRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getShoppingCart() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get the shoppingCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shoppingCart.getId().intValue()))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.updatedDate")
            .value(is(DEFAULT_UPDATED_DATE.toString()));
    }

    @Test
    void getShoppingCartsByIdFiltering() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        Long id = shoppingCart.getId();

        defaultShoppingCartFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShoppingCartFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShoppingCartFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllShoppingCartsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where createdDate equals to
        defaultShoppingCartFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllShoppingCartsByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where createdDate in
        defaultShoppingCartFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllShoppingCartsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where createdDate is not null
        defaultShoppingCartFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllShoppingCartsByUpdatedDateIsEqualToSomething() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where updatedDate equals to
        defaultShoppingCartFiltering("updatedDate.equals=" + DEFAULT_UPDATED_DATE, "updatedDate.equals=" + UPDATED_UPDATED_DATE);
    }

    @Test
    void getAllShoppingCartsByUpdatedDateIsInShouldWork() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where updatedDate in
        defaultShoppingCartFiltering(
            "updatedDate.in=" + DEFAULT_UPDATED_DATE + "," + UPDATED_UPDATED_DATE,
            "updatedDate.in=" + UPDATED_UPDATED_DATE
        );
    }

    @Test
    void getAllShoppingCartsByUpdatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList where updatedDate is not null
        defaultShoppingCartFiltering("updatedDate.specified=true", "updatedDate.specified=false");
    }

    @Test
    void getAllShoppingCartsByCustomerIsEqualToSomething() {
        Customer customer = CustomerResourceIT.createEntity();
        customerRepository.save(customer).block();
        Long customerId = customer.getId();
        shoppingCart.setCustomerId(customerId);
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();
        // Get all the shoppingCartList where customer equals to customerId
        defaultShoppingCartShouldBeFound("customerId.equals=" + customerId);

        // Get all the shoppingCartList where customer equals to (customerId + 1)
        defaultShoppingCartShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultShoppingCartFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultShoppingCartShouldBeFound(shouldBeFound);
        defaultShoppingCartShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShoppingCartShouldBeFound(String filter) {
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
            .value(hasItem(shoppingCart.getId().intValue()))
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
    private void defaultShoppingCartShouldNotBeFound(String filter) {
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
    void getNonExistingShoppingCart() {
        // Get the shoppingCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShoppingCart() throws Exception {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCart
        ShoppingCart updatedShoppingCart = shoppingCartRepository.findById(shoppingCart.getId()).block();
        updatedShoppingCart.createdDate(UPDATED_CREATED_DATE).updatedDate(UPDATED_UPDATED_DATE);
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(updatedShoppingCart);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shoppingCartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShoppingCartToMatchAllProperties(updatedShoppingCart);
    }

    @Test
    void putNonExistingShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shoppingCartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.createdDate(UPDATED_CREATED_DATE).updatedDate(UPDATED_UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShoppingCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoppingCartUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShoppingCart, shoppingCart),
            getPersistedShoppingCart(shoppingCart)
        );
    }

    @Test
    void fullUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.createdDate(UPDATED_CREATED_DATE).updatedDate(UPDATED_UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShoppingCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShoppingCartUpdatableFieldsEquals(partialUpdatedShoppingCart, getPersistedShoppingCart(partialUpdatedShoppingCart));
    }

    @Test
    void patchNonExistingShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shoppingCartDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShoppingCart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shoppingCart.setId(longCount.incrementAndGet());

        // Create the ShoppingCart
        ShoppingCartDTO shoppingCartDTO = shoppingCartMapper.toDto(shoppingCart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shoppingCartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCart in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShoppingCart() {
        // Initialize the database
        insertedShoppingCart = shoppingCartRepository.save(shoppingCart).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shoppingCart
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shoppingCartRepository.count().block();
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

    protected ShoppingCart getPersistedShoppingCart(ShoppingCart shoppingCart) {
        return shoppingCartRepository.findById(shoppingCart.getId()).block();
    }

    protected void assertPersistedShoppingCartToMatchAllProperties(ShoppingCart expectedShoppingCart) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShoppingCartAllPropertiesEquals(expectedShoppingCart, getPersistedShoppingCart(expectedShoppingCart));
        assertShoppingCartUpdatableFieldsEquals(expectedShoppingCart, getPersistedShoppingCart(expectedShoppingCart));
    }

    protected void assertPersistedShoppingCartToMatchUpdatableProperties(ShoppingCart expectedShoppingCart) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShoppingCartAllUpdatablePropertiesEquals(expectedShoppingCart, getPersistedShoppingCart(expectedShoppingCart));
        assertShoppingCartUpdatableFieldsEquals(expectedShoppingCart, getPersistedShoppingCart(expectedShoppingCart));
    }
}
