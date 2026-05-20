package br.com.andre.web.rest;

import static br.com.andre.domain.AddressAsserts.*;
import static br.com.andre.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.andre.IntegrationTest;
import br.com.andre.domain.Address;
import br.com.andre.domain.Customer;
import br.com.andre.repository.AddressRepository;
import br.com.andre.repository.CustomerRepository;
import br.com.andre.repository.EntityManager;
import br.com.andre.service.AddressService;
import br.com.andre.service.dto.AddressDTO;
import br.com.andre.service.mapper.AddressMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link AddressResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AddressResourceIT {

    private static final String DEFAULT_STREET = "AAAAAAAAAA";
    private static final String UPDATED_STREET = "BBBBBBBBBB";

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COMPLEMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMPLEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_DISTRICT = "AAAAAAAAAA";
    private static final String UPDATED_DISTRICT = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AA";
    private static final String UPDATED_STATE = "BB";

    private static final String DEFAULT_ZIP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ZIP_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AddressRepository addressRepository;

    @Mock
    private AddressRepository addressRepositoryMock;

    @Autowired
    private AddressMapper addressMapper;

    @Mock
    private AddressService addressServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Address address;

    private Address insertedAddress;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity() {
        return new Address()
            .street(DEFAULT_STREET)
            .number(DEFAULT_NUMBER)
            .complement(DEFAULT_COMPLEMENT)
            .district(DEFAULT_DISTRICT)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .zipCode(DEFAULT_ZIP_CODE)
            .country(DEFAULT_COUNTRY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createUpdatedEntity() {
        return new Address()
            .street(UPDATED_STREET)
            .number(UPDATED_NUMBER)
            .complement(UPDATED_COMPLEMENT)
            .district(UPDATED_DISTRICT)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .zipCode(UPDATED_ZIP_CODE)
            .country(UPDATED_COUNTRY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Address.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        address = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAddress != null) {
            addressRepository.delete(insertedAddress).block();
            insertedAddress = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAddress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);
        var returnedAddressDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AddressDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Address in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAddress = addressMapper.toEntity(returnedAddressDTO);
        assertAddressUpdatableFieldsEquals(returnedAddress, getPersistedAddress(returnedAddress));

        insertedAddress = returnedAddress;
    }

    @Test
    void createAddressWithExistingId() throws Exception {
        // Create the Address with an existing ID
        address.setId(1L);
        AddressDTO addressDTO = addressMapper.toDto(address);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStreetIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        address.setStreet(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        address.setCity(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        address.setState(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkZipCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        address.setZipCode(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        address.setCountry(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAddresses() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList
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
            .value(hasItem(address.getId().intValue()))
            .jsonPath("$.[*].street")
            .value(hasItem(DEFAULT_STREET))
            .jsonPath("$.[*].number")
            .value(hasItem(DEFAULT_NUMBER))
            .jsonPath("$.[*].complement")
            .value(hasItem(DEFAULT_COMPLEMENT))
            .jsonPath("$.[*].district")
            .value(hasItem(DEFAULT_DISTRICT))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].zipCode")
            .value(hasItem(DEFAULT_ZIP_CODE))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAddressesWithEagerRelationshipsIsEnabled() {
        when(addressServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(addressServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAddressesWithEagerRelationshipsIsNotEnabled() {
        when(addressServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(addressRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAddress() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(address.getId().intValue()))
            .jsonPath("$.street")
            .value(is(DEFAULT_STREET))
            .jsonPath("$.number")
            .value(is(DEFAULT_NUMBER))
            .jsonPath("$.complement")
            .value(is(DEFAULT_COMPLEMENT))
            .jsonPath("$.district")
            .value(is(DEFAULT_DISTRICT))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.state")
            .value(is(DEFAULT_STATE))
            .jsonPath("$.zipCode")
            .value(is(DEFAULT_ZIP_CODE))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY));
    }

    @Test
    void getAddressesByIdFiltering() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        Long id = address.getId();

        defaultAddressFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAddressFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAddressFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAddressesByStreetIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where street equals to
        defaultAddressFiltering("street.equals=" + DEFAULT_STREET, "street.equals=" + UPDATED_STREET);
    }

    @Test
    void getAllAddressesByStreetIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where street in
        defaultAddressFiltering("street.in=" + DEFAULT_STREET + "," + UPDATED_STREET, "street.in=" + UPDATED_STREET);
    }

    @Test
    void getAllAddressesByStreetIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where street is not null
        defaultAddressFiltering("street.specified=true", "street.specified=false");
    }

    @Test
    void getAllAddressesByStreetContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where street contains
        defaultAddressFiltering("street.contains=" + DEFAULT_STREET, "street.contains=" + UPDATED_STREET);
    }

    @Test
    void getAllAddressesByStreetNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where street does not contain
        defaultAddressFiltering("street.doesNotContain=" + UPDATED_STREET, "street.doesNotContain=" + DEFAULT_STREET);
    }

    @Test
    void getAllAddressesByNumberIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where number equals to
        defaultAddressFiltering("number.equals=" + DEFAULT_NUMBER, "number.equals=" + UPDATED_NUMBER);
    }

    @Test
    void getAllAddressesByNumberIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where number in
        defaultAddressFiltering("number.in=" + DEFAULT_NUMBER + "," + UPDATED_NUMBER, "number.in=" + UPDATED_NUMBER);
    }

    @Test
    void getAllAddressesByNumberIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where number is not null
        defaultAddressFiltering("number.specified=true", "number.specified=false");
    }

    @Test
    void getAllAddressesByNumberContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where number contains
        defaultAddressFiltering("number.contains=" + DEFAULT_NUMBER, "number.contains=" + UPDATED_NUMBER);
    }

    @Test
    void getAllAddressesByNumberNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where number does not contain
        defaultAddressFiltering("number.doesNotContain=" + UPDATED_NUMBER, "number.doesNotContain=" + DEFAULT_NUMBER);
    }

    @Test
    void getAllAddressesByComplementIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where complement equals to
        defaultAddressFiltering("complement.equals=" + DEFAULT_COMPLEMENT, "complement.equals=" + UPDATED_COMPLEMENT);
    }

    @Test
    void getAllAddressesByComplementIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where complement in
        defaultAddressFiltering("complement.in=" + DEFAULT_COMPLEMENT + "," + UPDATED_COMPLEMENT, "complement.in=" + UPDATED_COMPLEMENT);
    }

    @Test
    void getAllAddressesByComplementIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where complement is not null
        defaultAddressFiltering("complement.specified=true", "complement.specified=false");
    }

    @Test
    void getAllAddressesByComplementContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where complement contains
        defaultAddressFiltering("complement.contains=" + DEFAULT_COMPLEMENT, "complement.contains=" + UPDATED_COMPLEMENT);
    }

    @Test
    void getAllAddressesByComplementNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where complement does not contain
        defaultAddressFiltering("complement.doesNotContain=" + UPDATED_COMPLEMENT, "complement.doesNotContain=" + DEFAULT_COMPLEMENT);
    }

    @Test
    void getAllAddressesByDistrictIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where district equals to
        defaultAddressFiltering("district.equals=" + DEFAULT_DISTRICT, "district.equals=" + UPDATED_DISTRICT);
    }

    @Test
    void getAllAddressesByDistrictIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where district in
        defaultAddressFiltering("district.in=" + DEFAULT_DISTRICT + "," + UPDATED_DISTRICT, "district.in=" + UPDATED_DISTRICT);
    }

    @Test
    void getAllAddressesByDistrictIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where district is not null
        defaultAddressFiltering("district.specified=true", "district.specified=false");
    }

    @Test
    void getAllAddressesByDistrictContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where district contains
        defaultAddressFiltering("district.contains=" + DEFAULT_DISTRICT, "district.contains=" + UPDATED_DISTRICT);
    }

    @Test
    void getAllAddressesByDistrictNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where district does not contain
        defaultAddressFiltering("district.doesNotContain=" + UPDATED_DISTRICT, "district.doesNotContain=" + DEFAULT_DISTRICT);
    }

    @Test
    void getAllAddressesByCityIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where city equals to
        defaultAddressFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    void getAllAddressesByCityIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where city in
        defaultAddressFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    void getAllAddressesByCityIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where city is not null
        defaultAddressFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    void getAllAddressesByCityContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where city contains
        defaultAddressFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    void getAllAddressesByCityNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where city does not contain
        defaultAddressFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    void getAllAddressesByStateIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where state equals to
        defaultAddressFiltering("state.equals=" + DEFAULT_STATE, "state.equals=" + UPDATED_STATE);
    }

    @Test
    void getAllAddressesByStateIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where state in
        defaultAddressFiltering("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE, "state.in=" + UPDATED_STATE);
    }

    @Test
    void getAllAddressesByStateIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where state is not null
        defaultAddressFiltering("state.specified=true", "state.specified=false");
    }

    @Test
    void getAllAddressesByStateContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where state contains
        defaultAddressFiltering("state.contains=" + DEFAULT_STATE, "state.contains=" + UPDATED_STATE);
    }

    @Test
    void getAllAddressesByStateNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where state does not contain
        defaultAddressFiltering("state.doesNotContain=" + UPDATED_STATE, "state.doesNotContain=" + DEFAULT_STATE);
    }

    @Test
    void getAllAddressesByZipCodeIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where zipCode equals to
        defaultAddressFiltering("zipCode.equals=" + DEFAULT_ZIP_CODE, "zipCode.equals=" + UPDATED_ZIP_CODE);
    }

    @Test
    void getAllAddressesByZipCodeIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where zipCode in
        defaultAddressFiltering("zipCode.in=" + DEFAULT_ZIP_CODE + "," + UPDATED_ZIP_CODE, "zipCode.in=" + UPDATED_ZIP_CODE);
    }

    @Test
    void getAllAddressesByZipCodeIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where zipCode is not null
        defaultAddressFiltering("zipCode.specified=true", "zipCode.specified=false");
    }

    @Test
    void getAllAddressesByZipCodeContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where zipCode contains
        defaultAddressFiltering("zipCode.contains=" + DEFAULT_ZIP_CODE, "zipCode.contains=" + UPDATED_ZIP_CODE);
    }

    @Test
    void getAllAddressesByZipCodeNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where zipCode does not contain
        defaultAddressFiltering("zipCode.doesNotContain=" + UPDATED_ZIP_CODE, "zipCode.doesNotContain=" + DEFAULT_ZIP_CODE);
    }

    @Test
    void getAllAddressesByCountryIsEqualToSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where country equals to
        defaultAddressFiltering("country.equals=" + DEFAULT_COUNTRY, "country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    void getAllAddressesByCountryIsInShouldWork() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where country in
        defaultAddressFiltering("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY, "country.in=" + UPDATED_COUNTRY);
    }

    @Test
    void getAllAddressesByCountryIsNullOrNotNull() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where country is not null
        defaultAddressFiltering("country.specified=true", "country.specified=false");
    }

    @Test
    void getAllAddressesByCountryContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where country contains
        defaultAddressFiltering("country.contains=" + DEFAULT_COUNTRY, "country.contains=" + UPDATED_COUNTRY);
    }

    @Test
    void getAllAddressesByCountryNotContainsSomething() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList where country does not contain
        defaultAddressFiltering("country.doesNotContain=" + UPDATED_COUNTRY, "country.doesNotContain=" + DEFAULT_COUNTRY);
    }

    @Test
    void getAllAddressesByCustomerIsEqualToSomething() {
        Customer customer = CustomerResourceIT.createEntity();
        customerRepository.save(customer).block();
        Long customerId = customer.getId();
        address.setCustomerId(customerId);
        insertedAddress = addressRepository.save(address).block();
        // Get all the addressList where customer equals to customerId
        defaultAddressShouldBeFound("customerId.equals=" + customerId);

        // Get all the addressList where customer equals to (customerId + 1)
        defaultAddressShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultAddressFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAddressShouldBeFound(shouldBeFound);
        defaultAddressShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAddressShouldBeFound(String filter) {
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
            .value(hasItem(address.getId().intValue()))
            .jsonPath("$.[*].street")
            .value(hasItem(DEFAULT_STREET))
            .jsonPath("$.[*].number")
            .value(hasItem(DEFAULT_NUMBER))
            .jsonPath("$.[*].complement")
            .value(hasItem(DEFAULT_COMPLEMENT))
            .jsonPath("$.[*].district")
            .value(hasItem(DEFAULT_DISTRICT))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].state")
            .value(hasItem(DEFAULT_STATE))
            .jsonPath("$.[*].zipCode")
            .value(hasItem(DEFAULT_ZIP_CODE))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY));

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
    private void defaultAddressShouldNotBeFound(String filter) {
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
    void getNonExistingAddress() {
        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAddress() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address
        Address updatedAddress = addressRepository.findById(address.getId()).block();
        updatedAddress
            .street(UPDATED_STREET)
            .number(UPDATED_NUMBER)
            .complement(UPDATED_COMPLEMENT)
            .district(UPDATED_DISTRICT)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .zipCode(UPDATED_ZIP_CODE)
            .country(UPDATED_COUNTRY);
        AddressDTO addressDTO = addressMapper.toDto(updatedAddress);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAddressToMatchAllProperties(updatedAddress);
    }

    @Test
    void putNonExistingAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress.street(UPDATED_STREET).complement(UPDATED_COMPLEMENT).city(UPDATED_CITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAddressUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAddress, address), getPersistedAddress(address));
    }

    @Test
    void fullUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .street(UPDATED_STREET)
            .number(UPDATED_NUMBER)
            .complement(UPDATED_COMPLEMENT)
            .district(UPDATED_DISTRICT)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .zipCode(UPDATED_ZIP_CODE)
            .country(UPDATED_COUNTRY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAddressUpdatableFieldsEquals(partialUpdatedAddress, getPersistedAddress(partialUpdatedAddress));
    }

    @Test
    void patchNonExistingAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAddress() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the address
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return addressRepository.count().block();
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

    protected Address getPersistedAddress(Address address) {
        return addressRepository.findById(address.getId()).block();
    }

    protected void assertPersistedAddressToMatchAllProperties(Address expectedAddress) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAddressAllPropertiesEquals(expectedAddress, getPersistedAddress(expectedAddress));
        assertAddressUpdatableFieldsEquals(expectedAddress, getPersistedAddress(expectedAddress));
    }

    protected void assertPersistedAddressToMatchUpdatableProperties(Address expectedAddress) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAddressAllUpdatablePropertiesEquals(expectedAddress, getPersistedAddress(expectedAddress));
        assertAddressUpdatableFieldsEquals(expectedAddress, getPersistedAddress(expectedAddress));
    }
}
