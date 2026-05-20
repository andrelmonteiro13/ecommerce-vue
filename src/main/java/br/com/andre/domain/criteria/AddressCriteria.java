package br.com.andre.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link br.com.andre.domain.Address} entity. This class is used
 * in {@link br.com.andre.web.rest.AddressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /addresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AddressCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter street;

    private StringFilter number;

    private StringFilter complement;

    private StringFilter district;

    private StringFilter city;

    private StringFilter state;

    private StringFilter zipCode;

    private StringFilter country;

    private LongFilter customerId;

    private Boolean distinct;

    public AddressCriteria() {}

    public AddressCriteria(AddressCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.street = other.optionalStreet().map(StringFilter::copy).orElse(null);
        this.number = other.optionalNumber().map(StringFilter::copy).orElse(null);
        this.complement = other.optionalComplement().map(StringFilter::copy).orElse(null);
        this.district = other.optionalDistrict().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.state = other.optionalState().map(StringFilter::copy).orElse(null);
        this.zipCode = other.optionalZipCode().map(StringFilter::copy).orElse(null);
        this.country = other.optionalCountry().map(StringFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AddressCriteria copy() {
        return new AddressCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getStreet() {
        return street;
    }

    public Optional<StringFilter> optionalStreet() {
        return Optional.ofNullable(street);
    }

    public StringFilter street() {
        if (street == null) {
            setStreet(new StringFilter());
        }
        return street;
    }

    public void setStreet(StringFilter street) {
        this.street = street;
    }

    public StringFilter getNumber() {
        return number;
    }

    public Optional<StringFilter> optionalNumber() {
        return Optional.ofNullable(number);
    }

    public StringFilter number() {
        if (number == null) {
            setNumber(new StringFilter());
        }
        return number;
    }

    public void setNumber(StringFilter number) {
        this.number = number;
    }

    public StringFilter getComplement() {
        return complement;
    }

    public Optional<StringFilter> optionalComplement() {
        return Optional.ofNullable(complement);
    }

    public StringFilter complement() {
        if (complement == null) {
            setComplement(new StringFilter());
        }
        return complement;
    }

    public void setComplement(StringFilter complement) {
        this.complement = complement;
    }

    public StringFilter getDistrict() {
        return district;
    }

    public Optional<StringFilter> optionalDistrict() {
        return Optional.ofNullable(district);
    }

    public StringFilter district() {
        if (district == null) {
            setDistrict(new StringFilter());
        }
        return district;
    }

    public void setDistrict(StringFilter district) {
        this.district = district;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getState() {
        return state;
    }

    public Optional<StringFilter> optionalState() {
        return Optional.ofNullable(state);
    }

    public StringFilter state() {
        if (state == null) {
            setState(new StringFilter());
        }
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getZipCode() {
        return zipCode;
    }

    public Optional<StringFilter> optionalZipCode() {
        return Optional.ofNullable(zipCode);
    }

    public StringFilter zipCode() {
        if (zipCode == null) {
            setZipCode(new StringFilter());
        }
        return zipCode;
    }

    public void setZipCode(StringFilter zipCode) {
        this.zipCode = zipCode;
    }

    public StringFilter getCountry() {
        return country;
    }

    public Optional<StringFilter> optionalCountry() {
        return Optional.ofNullable(country);
    }

    public StringFilter country() {
        if (country == null) {
            setCountry(new StringFilter());
        }
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AddressCriteria that = (AddressCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(street, that.street) &&
            Objects.equals(number, that.number) &&
            Objects.equals(complement, that.complement) &&
            Objects.equals(district, that.district) &&
            Objects.equals(city, that.city) &&
            Objects.equals(state, that.state) &&
            Objects.equals(zipCode, that.zipCode) &&
            Objects.equals(country, that.country) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, number, complement, district, city, state, zipCode, country, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStreet().map(f -> "street=" + f + ", ").orElse("") +
            optionalNumber().map(f -> "number=" + f + ", ").orElse("") +
            optionalComplement().map(f -> "complement=" + f + ", ").orElse("") +
            optionalDistrict().map(f -> "district=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalState().map(f -> "state=" + f + ", ").orElse("") +
            optionalZipCode().map(f -> "zipCode=" + f + ", ").orElse("") +
            optionalCountry().map(f -> "country=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
