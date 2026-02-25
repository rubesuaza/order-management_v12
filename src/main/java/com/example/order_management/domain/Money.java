package com.example.order_management.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Money {

    private static final String DEFAULT_CURRENCY = "USD";

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be null or blank");
        }
        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    public static Money of(BigDecimal amount, String currency) {
        String effectiveCurrency = (currency == null || currency.isBlank()) ? DEFAULT_CURRENCY : currency;
        return new Money(amount, effectiveCurrency);
        }

    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int multiplier) {
        BigDecimal factor = BigDecimal.valueOf(multiplier);
        return new Money(this.amount.multiply(factor), this.currency);
    }

    private void ensureSameCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money must not be null");
        }
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException("Currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0 &&
                currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

