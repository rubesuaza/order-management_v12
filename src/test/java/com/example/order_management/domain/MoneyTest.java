package com.example.order_management.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Suma correctamente dos cantidades con la misma moneda")
    void shouldAddTwoMoneyWithSameCurrency() {
        Money m1 = Money.of(new BigDecimal("10.50"), "USD");
        Money m2 = Money.of(new BigDecimal("5.25"), "USD");

        Money result = m1.add(m2);

        assertThat(result.getAmount()).isEqualByComparingTo("15.75");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Resta correctamente dos cantidades con la misma moneda")
    void shouldSubtractTwoMoneyWithSameCurrency() {
        Money m1 = Money.of(new BigDecimal("10.50"), "USD");
        Money m2 = Money.of(new BigDecimal("5.25"), "USD");

        Money result = m1.subtract(m2);

        assertThat(result.getAmount()).isEqualByComparingTo("5.25");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Multiplica correctamente una cantidad por un entero")
    void shouldMultiplyMoneyByInteger() {
        Money m1 = Money.of(new BigDecimal("10.00"), "USD");

        Money result = m1.multiply(3);

        assertThat(result.getAmount()).isEqualByComparingTo("30.00");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Lanza CurrencyMismatchException al operar con distintas monedas")
    void shouldThrowWhenAddingDifferentCurrencies() {
        Money usd = Money.of(new BigDecimal("10.00"), "USD");
        Money eur = Money.of(new BigDecimal("10.00"), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }
}

