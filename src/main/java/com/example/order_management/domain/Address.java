package com.example.order_management.domain;

import java.util.Objects;

public final class Address {

    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;

    public Address(String street, String city, String zipCode, String country) {
        this.street = requireNonBlank(street, "street");
        this.city = requireNonBlank(city, "city");
        this.zipCode = requireNonBlank(zipCode, "zipCode");
        this.country = requireNonBlank(country, "country");
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Address " + fieldName + " must not be null or blank") {};
        }
        return value;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(zipCode, address.zipCode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country);
    }

    @Override
    public String toString() {
        return street + ", " + city + " " + zipCode + ", " + country;
    }
}

