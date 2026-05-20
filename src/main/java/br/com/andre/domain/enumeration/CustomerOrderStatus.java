package br.com.andre.domain.enumeration;

/**
 * The CustomerOrderStatus enumeration.
 */
public enum CustomerOrderStatus {
    CREATED,
    WAITING_PAYMENT,
    PAID,
    SEPARATING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
}
