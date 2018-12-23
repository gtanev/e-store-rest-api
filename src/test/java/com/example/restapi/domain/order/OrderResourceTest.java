package com.example.restapi.domain.order;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class OrderResourceTest {

  private final Link selfLink = new Link("https://localhost:8443/orders/1", "self");
  private final Link profileLink = new Link("https://localhost:8443/profile/orders/1", "profile");

  @Test
  public void testProcessedResource() {
    final OrderResource orderResource = new OrderResource();
    final Order order = new Order("george.tanev@valid-domain.com");
    final Resource<Order> originalResource = new Resource<>(order);

    originalResource.add(Arrays.asList(selfLink, profileLink));

    final Resource<Order> processedResource = orderResource.process(originalResource);

    assertEquals(order, processedResource.getContent());
    assertTrue(processedResource.hasLink("self"));
    assertFalse(processedResource.hasLink("profile"));
    assertEquals(1, processedResource.getLinks().size());
  }

}
