package com.example.restapi.domain.order_product;

import com.example.restapi.domain.order.Order;
import com.example.restapi.domain.product.Product;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProductTest {

  private OrderProductId id;
  private Order order;
  private Product product;
  private BigDecimal price;
  private Integer quantity;

  @BeforeEach
  void init() {
    order = new Order("george.tanev@valid-domain.com");
    order.setId(0L);
    product = new Product("Product 1", BigDecimal.valueOf(5.75));
    product.setId(Long.MAX_VALUE);
    quantity = 50;
    price = new BigDecimal(2.75);
    id = new OrderProductId(order.getId(), product.getId());
  }

  @Test
  public void testNoArgConstructor() {
    final OrderProduct orderProduct = new OrderProduct();

    assertNotNull(orderProduct);
    assertNull(orderProduct.getId());
    assertNull(orderProduct.getOrder());
    assertNull(orderProduct.getProduct());
    assertNull(orderProduct.getPrice());
    assertNull(orderProduct.getQuantity());
  }

  @Test
  public void testParameterizedConstructor() {
    final OrderProduct orderProduct = new OrderProduct(order, product, quantity, price);

    assertNotNull(orderProduct);
    assertNotNull(orderProduct.getId());
    assertEquals(order.getId(), orderProduct.getId().getOrderId());
    assertEquals(product.getId(), orderProduct.getId().getProductId());
    assertProperties(orderProduct);
  }

  @Test
  public void testSetters() {
    final OrderProduct orderProduct = new OrderProduct();
    orderProduct.setOrder(order);
    orderProduct.setProduct(product);
    orderProduct.setPrice(price);
    orderProduct.setQuantity(quantity);

    assertNotNull(orderProduct);
    assertNull(orderProduct.getId());
    assertProperties(orderProduct);
  }

  @Test
  public void testEqualsHashCode() {
    EqualsVerifier.forClass(OrderProduct.class)
        .withPrefabValues(Order.class, new Order(), order)
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
        .suppress(Warning.STRICT_HASHCODE)
        .verify();
  }

  private void assertProperties(OrderProduct orderProduct) {
    assertEquals(order, orderProduct.getOrder());
    assertEquals(product, orderProduct.getProduct());
    assertEquals(product.getName(), orderProduct.getProductName());
    assertEquals(price, orderProduct.getPrice());
    assertEquals(quantity, orderProduct.getQuantity());
  }

}
