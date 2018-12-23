package com.example.restapi.domain.product;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProductResource implements ResourceProcessor<Resource<Product>> {

  @Override
  public Resource<Product> process(Resource<Product> resource) {
    return new Resource<>(resource.getContent(), resource.getLink(Link.REL_SELF));
  }
}
