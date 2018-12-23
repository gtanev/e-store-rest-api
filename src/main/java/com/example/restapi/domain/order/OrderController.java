package com.example.restapi.domain.order;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Override OrderRepository save() method for POST requests (disallows PUT and PATCH requests implicitly)
 */
@RepositoryRestController
public class OrderController {

  private final OrderRepository repository;

  @Autowired
  public OrderController(OrderRepository repository) {
    this.repository = repository;
  }

  @PostMapping("/orders")
  @ApiOperation("Creates a new order.")
  @ApiParam("The order object that needs to be created.")
  public ResponseEntity<?> post(@RequestBody Resource<Order> body,
                                PersistentEntityResourceAssembler resourceAssembler) {

    if (body.getContent() == null) return ResponseEntity.badRequest().build();

    final Order order = repository.save(body.getContent());

    return ResponseEntity.status(HttpStatus.CREATED).body(resourceAssembler.toResource(order));
  }
}
