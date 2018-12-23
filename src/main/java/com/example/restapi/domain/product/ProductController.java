package com.example.restapi.domain.product;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

/**
 * Override ProductRepository save() method for POST and PATCH requests and disallow PUT requests
 */
@RepositoryRestController
public class ProductController {

  private final ProductRepository repository;

  @Autowired
  public ProductController(ProductRepository repository) {
    this.repository = repository;
  }

  @PostMapping("/products")
  @ApiOperation(value = "Creates a new product.")
  public ResponseEntity<?> save(@RequestBody @ApiParam("The product object that needs to be created.") Resource<Product> body,
                                PersistentEntityResourceAssembler resourceAssembler) {
    if (body.getContent() == null || body.getContent().getName() == null || body.getContent().getPrice() == null)
      return ResponseEntity.badRequest().build();

    final Optional<Product> existingProduct = repository.findByName(body.getContent().getName());

    if (existingProduct.isPresent()) {
      final String existingProductUri
          = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + existingProduct.get().getId();

      return ResponseEntity.status(HttpStatus.CONFLICT)
          .header(HttpHeaders.LOCATION, existingProductUri)
          .build();
    }

    final Product product = repository.save(body.getContent());

    return ResponseEntity.status(HttpStatus.CREATED).body(resourceAssembler.toResource(product));
  }

  @PatchMapping("/products/{id}")
  @ApiOperation(value = "Updates an existing product.")
  public ResponseEntity<?> patch(@PathVariable("id") @ApiParam("The id of the product.") final String id,
                                 @RequestBody @ApiParam("The product object that needs to be updated.") Resource<Product> body,
                                 PersistentEntityResourceAssembler resourceAssembler) {
    if (body.getContent() == null || body.getContent().getPrice() == null)
      return ResponseEntity.badRequest().build();

    final Optional<Product> product = repository.findById(Long.valueOf(id));

    if (!product.isPresent())
      return ResponseEntity.noContent().build();

    product.get().setPrice(body.getContent().getPrice());

    return ResponseEntity.ok(resourceAssembler.toResource(repository.save(product.get())));
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<?> put(@PathVariable("id") final String ignored) {
    final String[] requestTypes = {HttpMethod.GET.name(), HttpMethod.PATCH.name()};

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .header(HttpHeaders.ALLOW, requestTypes)
        .build();
  }
}
