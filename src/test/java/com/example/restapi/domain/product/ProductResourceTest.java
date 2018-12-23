package com.example.restapi.domain.product;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ProductResourceTest {

  private final Link selfLink = new Link("https://localhost:8443/products/1", "self");
  private final Link profileLink = new Link("https://localhost:8443/profile/products/1", "profile");

  @Test
  public void testProcessedResource() {
    final ProductResource productResource = new ProductResource();
    final Product product = new Product("Pen", BigDecimal.valueOf(10));
    final Resource<Product> originalResource = new Resource<>(product);

    originalResource.add(Arrays.asList(selfLink, profileLink));

    final Resource<Product> processedResource = productResource.process(originalResource);

    assertEquals(product, processedResource.getContent());
    assertTrue(processedResource.hasLink("self"));
    assertFalse(processedResource.hasLink("profile"));
    assertEquals(1, processedResource.getLinks().size());
  }

}
