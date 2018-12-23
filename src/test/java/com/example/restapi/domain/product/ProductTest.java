package com.example.restapi.domain.product;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

  private Long id;
  private String name;
  private BigDecimal price;

  @BeforeEach
  void init() {
    id = 12L;
    name = "Product 1";
    price = new BigDecimal(2.75);
  }

  @Test
  public void testNoArgConstructor() {
    final Product product = new Product();

    assertNotNull(product);
    assertNull(product.getId());
    assertNull(product.getPrice());
    assertNull(product.getName());
  }

  @Test
  public void testParameterizedConstructor() {
    final Product product = new Product(name, price);

    assertNotNull(product);
    assertEquals(name, product.getName());
    assertEquals(price, product.getPrice());
  }

  @Test
  public void testSetters() {
    final Product product = new Product();
    product.setId(id);
    product.setName(name);
    product.setPrice(price);

    assertNotNull(product);
    assertEquals(id, product.getId());
    assertEquals(name, product.getName());
    assertEquals(price, product.getPrice());
  }

  @Test
  public void testEqualsHashCode() {
    EqualsVerifier.forClass(Product.class)
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
        .verify();
  }

}
