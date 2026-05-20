package br.com.andre.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.andre.domain.Address} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AddressDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 150)
    private String street;

    @Size(max = 20)
    private String number;

    @Size(max = 100)
    private String complement;

    @Size(max = 100)
    private String district;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    private String city;

    @NotNull(message = "must not be null")
    @Size(max = 2)
    private String state;

    @NotNull(message = "must not be null")
    @Size(max = 20)
    private String zipCode;

    @NotNull(message = "must not be null")
    @Size(max = 80)
    private String country;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressDTO)) {
            return false;
        }

        AddressDTO addressDTO = (AddressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, addressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressDTO{" +
            "id=" + getId() +
            ", street='" + getStreet() + "'" +
            ", number='" + getNumber() + "'" +
            ", complement='" + getComplement() + "'" +
            ", district='" + getDistrict() + "'" +
            ", city='" + getCity() + "'" +
            ", state='" + getState() + "'" +
            ", zipCode='" + getZipCode() + "'" +
            ", country='" + getCountry() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
