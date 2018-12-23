package com.example.restapi.domain.order_product;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProductIdTest {

  private Long orderId = 12345L;
  private Long productId = 43210L;

  @Test
  public void testPrivateNoArgConstructor() {
    try {
      final Constructor<OrderProductId> constructor = OrderProductId.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
        constructor.newInstance();
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
        fail("OrderProductId private no-arg constructor is inaccessible.");
      }
    } catch (NoSuchMethodException e) {
      fail("OrderProductId is missing a private no-arg constructor");
    }
  }

  @Test
  public void testParameterizedConstructor() {
    final OrderProductId id = new OrderProductId(orderId, productId);

    assertNotNull(id);
    assertEquals(orderId, id.getOrderId());
    assertEquals(productId, id.getProductId());
  }

  @Test
  public void testSetters() {
    final OrderProductId id = new OrderProductId(orderId, productId);

    id.setOrderId(null);
    id.setProductId(null);

    assertNotNull(id);
    assertNull(id.getOrderId());
    assertNull(id.getProductId());
  }

  @Test
  public void testEqualsHashCode() {
    EqualsVerifier.forClass(OrderProductId.class).verify();
  }

}
