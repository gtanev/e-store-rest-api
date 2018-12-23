package com.example.restapi.domain.order;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class OrderResource implements ResourceProcessor<Resource<Order>> {

  @Override
  public Resource<Order> process(Resource<Order> resource) {
    return new Resource<>(resource.getContent(), resource.getLink(Link.REL_SELF));
  }
}
