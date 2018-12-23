package com.example.restapi.domain.order;

import com.example.restapi.domain.order_product.OrderProduct;
import com.example.restapi.domain.product.Product;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

  private String validEmail;
  private Product product1, product2;

  @BeforeEach
  void init() {
    validEmail = "george.tanev@valid-domain.com";
    product1 = new Product("Product #1", BigDecimal.valueOf(5.75));
    product2 = new Product("Product #2", BigDecimal.valueOf(20));
  }

  @Test
  public void testNoArgConstructor() {
    final Order order = new Order();

    assertNotNull(order);
    assertNull(order.getId());
    assertNull(order.getCreatedAt());
    assertNull(order.getCustomerEmail());
    assertEquals(Collections.EMPTY_LIST, order.getProducts());
    assertEquals(BigDecimal.ZERO, order.getOrderTotal());
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/valid_emails.txt", delimiter = '\n')
  public void testParameterizedConstructor(String email) {
    final Order order = new Order(email);

    assertNotNull(order);
    assertEquals(email, order.getCustomerEmail());
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/invalid_emails.txt", delimiter = '\n')
  public void testParameterizedConstructor_InvalidEmail(String email) {
    assertThrows(IllegalArgumentException.class, () -> new Order(email));
  }

  @Test
  public void testSetters() {
    final Order order = new Order();
    order.setId(130L);
    order.setCustomerEmail(validEmail);

    assertNotNull(order);
    assertEquals(Long.valueOf(130L), order.getId());
    assertEquals(validEmail, order.getCustomerEmail());
    assertTrue(order.getProducts().isEmpty());
    assertEquals(BigDecimal.ZERO, order.getOrderTotal());

    order.addProduct(product1, 2);
    order.addProduct(product2, 1);

    final Set<Product> products = order.getProducts()
        .stream()
        .map(OrderProduct::getProduct)
        .collect(Collectors.toSet());

    assertEquals(2, order.getProducts().size());
    assertTrue(products.contains(product1));
    assertTrue(products.contains(product2));
    assertEquals(product1.getPrice().multiply(BigDecimal.valueOf(2))
        .add(product2.getPrice().multiply(BigDecimal.valueOf(1))), order.getOrderTotal());
  }

  @Test
  public void testEqualsHashCode() {
    EqualsVerifier.forClass(Order.class)
        .withPrefabValues(OrderProduct.class,
            new OrderProduct(new Order(validEmail), product1, 1, BigDecimal.TEN),
            new OrderProduct(new Order(validEmail), product2, 100, BigDecimal.valueOf(1.25)))
        .usingGetClass()
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
        .suppress(Warning.STRICT_HASHCODE)
        .verify();
  }
}
