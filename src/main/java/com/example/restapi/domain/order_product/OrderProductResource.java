package com.example.restapi.domain.order_product;

import com.example.restapi.domain.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class OrderProductResource implements ResourceProcessor<Resource<OrderProduct>> {

  private final EntityLinks entityLinks;

  @Autowired
  public OrderProductResource(EntityLinks entityLinks) {
    this.entityLinks = entityLinks;
  }

  @Override
  public Resource<OrderProduct> process(Resource<OrderProduct> resource) {
    final Link productLink
        = entityLinks.linkToSingleResource(Product.class, resource.getContent().getProduct().getId());

    return new Resource<>(resource.getContent(), productLink.withSelfRel());
  }
}
