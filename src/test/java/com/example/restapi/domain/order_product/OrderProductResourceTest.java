package com.example.restapi.domain.order_product;

import com.example.restapi.domain.order.Order;
import com.example.restapi.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderProductResourceTest {

  @Mock
  private EntityLinks entityLinks;
  private Link productLink;

  private OrderProductResource orderProductResource;
  private Order order;
  private Product product;
  private BigDecimal price;
  private Integer quantity;

  @BeforeEach
  void init() {
    MockitoAnnotations.initMocks(this);

    orderProductResource = new OrderProductResource(entityLinks);
    productLink = new Link("https://localhost:8443/products/1");
    order = new Order("george.tanev@valid-domain.com");
    order.setId(0L);
    product = new Product("Product 1", BigDecimal.valueOf(5.75));
    product.setId(1L);
    quantity = 50;
    price = new BigDecimal(2.75);
  }

  @Test
  public void testProcessedResource() {
    final OrderProduct orderProduct = new OrderProduct(order, product, quantity, price);
    final Resource<OrderProduct> originalResource = new Resource<>(orderProduct);

    when(entityLinks.linkToSingleResource(any(), any())).thenReturn(productLink);

    final Resource<OrderProduct> processedResource = orderProductResource.process(originalResource);

    verify(entityLinks).linkToSingleResource(any(), any());
    assertEquals(orderProduct, processedResource.getContent());
    assertTrue(processedResource.hasLink("self"));
    assertEquals(1, processedResource.getLinks().size());
    assertEquals(productLink, processedResource.getLink("self"));
  }
}
