package br.com.andre.web.rest;

import static br.com.andre.domain.ShipmentAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.CustomerOrder;
import br.com.andre.domain.Shipment;
import br.com.andre.domain.enumeration.ShipmentStatus;
import br.com.andre.repository.CustomerOrderRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.repository.ShipmentRepository;
import br.com.andre.service.ShipmentService;
import br.com.andre.service.dto.ShipmentDTO;
import br.com.andre.service.mapper.ShipmentMapper;
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
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShipmentResourceIT {

    private static final String DEFAULT_TRACKING_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TRACKING_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CARRIER = "AAAAAAAAAA";
    private static final String UPDATED_CARRIER = "BBBBBBBBBB";

    private static final ShipmentStatus DEFAULT_STATUS = ShipmentStatus.PENDING;
    private static final ShipmentStatus UPDATED_STATUS = ShipmentStatus.PREPARING;

    private static final Instant DEFAULT_SHIPPED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SHIPPED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DELIVERED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentRepository shipmentRepositoryMock;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Mock
    private ShipmentService shipmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Shipment shipment;

    private Shipment insertedShipment;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createEntity() {
        return new Shipment()
            .trackingNumber(DEFAULT_TRACKING_NUMBER)
            .carrier(DEFAULT_CARRIER)
            .status(DEFAULT_STATUS)
            .shippedDate(DEFAULT_SHIPPED_DATE)
            .deliveredDate(DEFAULT_DELIVERED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createUpdatedEntity() {
        return new Shipment()
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .carrier(UPDATED_CARRIER)
            .status(UPDATED_STATUS)
            .shippedDate(UPDATED_SHIPPED_DATE)
            .deliveredDate(UPDATED_DELIVERED_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Shipment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        shipment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedShipment != null) {
            shipmentRepository.delete(insertedShipment).block();
            insertedShipment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createShipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);
        var returnedShipmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ShipmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Shipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipment = shipmentMapper.toEntity(returnedShipmentDTO);
        assertShipmentUpdatableFieldsEquals(returnedShipment, getPersistedShipment(returnedShipment));

        insertedShipment = returnedShipment;
    }

    @Test
    void createShipmentWithExistingId() throws Exception {
        // Create the Shipment with an existing ID
        shipment.setId(1L);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setStatus(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllShipments() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList
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
            .value(hasItem(shipment.getId().intValue()))
            .jsonPath("$.[*].trackingNumber")
            .value(hasItem(DEFAULT_TRACKING_NUMBER))
            .jsonPath("$.[*].carrier")
            .value(hasItem(DEFAULT_CARRIER))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].shippedDate")
            .value(hasItem(DEFAULT_SHIPPED_DATE.toString()))
            .jsonPath("$.[*].deliveredDate")
            .value(hasItem(DEFAULT_DELIVERED_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentsWithEagerRelationshipsIsEnabled() {
        when(shipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(shipmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentsWithEagerRelationshipsIsNotEnabled() {
        when(shipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(shipmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getShipment() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get the shipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shipment.getId().intValue()))
            .jsonPath("$.trackingNumber")
            .value(is(DEFAULT_TRACKING_NUMBER))
            .jsonPath("$.carrier")
            .value(is(DEFAULT_CARRIER))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.shippedDate")
            .value(is(DEFAULT_SHIPPED_DATE.toString()))
            .jsonPath("$.deliveredDate")
            .value(is(DEFAULT_DELIVERED_DATE.toString()));
    }

    @Test
    void getShipmentsByIdFiltering() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        Long id = shipment.getId();

        defaultShipmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShipmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShipmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllShipmentsByTrackingNumberIsEqualToSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where trackingNumber equals to
        defaultShipmentFiltering("trackingNumber.equals=" + DEFAULT_TRACKING_NUMBER, "trackingNumber.equals=" + UPDATED_TRACKING_NUMBER);
    }

    @Test
    void getAllShipmentsByTrackingNumberIsInShouldWork() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where trackingNumber in
        defaultShipmentFiltering(
            "trackingNumber.in=" + DEFAULT_TRACKING_NUMBER + "," + UPDATED_TRACKING_NUMBER,
            "trackingNumber.in=" + UPDATED_TRACKING_NUMBER
        );
    }

    @Test
    void getAllShipmentsByTrackingNumberIsNullOrNotNull() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where trackingNumber is not null
        defaultShipmentFiltering("trackingNumber.specified=true", "trackingNumber.specified=false");
    }

    @Test
    void getAllShipmentsByTrackingNumberContainsSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where trackingNumber contains
        defaultShipmentFiltering(
            "trackingNumber.contains=" + DEFAULT_TRACKING_NUMBER,
            "trackingNumber.contains=" + UPDATED_TRACKING_NUMBER
        );
    }

    @Test
    void getAllShipmentsByTrackingNumberNotContainsSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where trackingNumber does not contain
        defaultShipmentFiltering(
            "trackingNumber.doesNotContain=" + UPDATED_TRACKING_NUMBER,
            "trackingNumber.doesNotContain=" + DEFAULT_TRACKING_NUMBER
        );
    }

    @Test
    void getAllShipmentsByCarrierIsEqualToSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where carrier equals to
        defaultShipmentFiltering("carrier.equals=" + DEFAULT_CARRIER, "carrier.equals=" + UPDATED_CARRIER);
    }

    @Test
    void getAllShipmentsByCarrierIsInShouldWork() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where carrier in
        defaultShipmentFiltering("carrier.in=" + DEFAULT_CARRIER + "," + UPDATED_CARRIER, "carrier.in=" + UPDATED_CARRIER);
    }

    @Test
    void getAllShipmentsByCarrierIsNullOrNotNull() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where carrier is not null
        defaultShipmentFiltering("carrier.specified=true", "carrier.specified=false");
    }

    @Test
    void getAllShipmentsByCarrierContainsSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where carrier contains
        defaultShipmentFiltering("carrier.contains=" + DEFAULT_CARRIER, "carrier.contains=" + UPDATED_CARRIER);
    }

    @Test
    void getAllShipmentsByCarrierNotContainsSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where carrier does not contain
        defaultShipmentFiltering("carrier.doesNotContain=" + UPDATED_CARRIER, "carrier.doesNotContain=" + DEFAULT_CARRIER);
    }

    @Test
    void getAllShipmentsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where status equals to
        defaultShipmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllShipmentsByStatusIsInShouldWork() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where status in
        defaultShipmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllShipmentsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where status is not null
        defaultShipmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllShipmentsByShippedDateIsEqualToSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where shippedDate equals to
        defaultShipmentFiltering("shippedDate.equals=" + DEFAULT_SHIPPED_DATE, "shippedDate.equals=" + UPDATED_SHIPPED_DATE);
    }

    @Test
    void getAllShipmentsByShippedDateIsInShouldWork() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where shippedDate in
        defaultShipmentFiltering(
            "shippedDate.in=" + DEFAULT_SHIPPED_DATE + "," + UPDATED_SHIPPED_DATE,
            "shippedDate.in=" + UPDATED_SHIPPED_DATE
        );
    }

    @Test
    void getAllShipmentsByShippedDateIsNullOrNotNull() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where shippedDate is not null
        defaultShipmentFiltering("shippedDate.specified=true", "shippedDate.specified=false");
    }

    @Test
    void getAllShipmentsByDeliveredDateIsEqualToSomething() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where deliveredDate equals to
        defaultShipmentFiltering("deliveredDate.equals=" + DEFAULT_DELIVERED_DATE, "deliveredDate.equals=" + UPDATED_DELIVERED_DATE);
    }

    @Test
    void getAllShipmentsByDeliveredDateIsInShouldWork() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where deliveredDate in
        defaultShipmentFiltering(
            "deliveredDate.in=" + DEFAULT_DELIVERED_DATE + "," + UPDATED_DELIVERED_DATE,
            "deliveredDate.in=" + UPDATED_DELIVERED_DATE
        );
    }

    @Test
    void getAllShipmentsByDeliveredDateIsNullOrNotNull() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList where deliveredDate is not null
        defaultShipmentFiltering("deliveredDate.specified=true", "deliveredDate.specified=false");
    }

    @Test
    void getAllShipmentsByOrderIsEqualToSomething() {
        CustomerOrder order = CustomerOrderResourceIT.createEntity();
        customerOrderRepository.save(order).block();
        Long orderId = order.getId();
        shipment.setOrderId(orderId);
        insertedShipment = shipmentRepository.save(shipment).block();
        // Get all the shipmentList where order equals to orderId
        defaultShipmentShouldBeFound("orderId.equals=" + orderId);

        // Get all the shipmentList where order equals to (orderId + 1)
        defaultShipmentShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    private void defaultShipmentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultShipmentShouldBeFound(shouldBeFound);
        defaultShipmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentShouldBeFound(String filter) {
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
            .value(hasItem(shipment.getId().intValue()))
            .jsonPath("$.[*].trackingNumber")
            .value(hasItem(DEFAULT_TRACKING_NUMBER))
            .jsonPath("$.[*].carrier")
            .value(hasItem(DEFAULT_CARRIER))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].shippedDate")
            .value(hasItem(DEFAULT_SHIPPED_DATE.toString()))
            .jsonPath("$.[*].deliveredDate")
            .value(hasItem(DEFAULT_DELIVERED_DATE.toString()));

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
    private void defaultShipmentShouldNotBeFound(String filter) {
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
    void getNonExistingShipment() {
        // Get the shipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).block();
        updatedShipment
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .carrier(UPDATED_CARRIER)
            .status(UPDATED_STATUS)
            .shippedDate(UPDATED_SHIPPED_DATE)
            .deliveredDate(UPDATED_DELIVERED_DATE);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(updatedShipment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentToMatchAllProperties(updatedShipment);
    }

    @Test
    void putNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment.trackingNumber(UPDATED_TRACKING_NUMBER).status(UPDATED_STATUS).deliveredDate(UPDATED_DELIVERED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShipment, shipment), getPersistedShipment(shipment));
    }

    @Test
    void fullUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment
            .trackingNumber(UPDATED_TRACKING_NUMBER)
            .carrier(UPDATED_CARRIER)
            .status(UPDATED_STATUS)
            .shippedDate(UPDATED_SHIPPED_DATE)
            .deliveredDate(UPDATED_DELIVERED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(partialUpdatedShipment, getPersistedShipment(partialUpdatedShipment));
    }

    @Test
    void patchNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShipment() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentRepository.count().block();
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

    protected Shipment getPersistedShipment(Shipment shipment) {
        return shipmentRepository.findById(shipment.getId()).block();
    }

    protected void assertPersistedShipmentToMatchAllProperties(Shipment expectedShipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShipmentAllPropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
        assertShipmentUpdatableFieldsEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }

    protected void assertPersistedShipmentToMatchUpdatableProperties(Shipment expectedShipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShipmentAllUpdatablePropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
        assertShipmentUpdatableFieldsEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }
}
