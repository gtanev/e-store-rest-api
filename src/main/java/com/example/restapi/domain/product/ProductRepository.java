package com.example.restapi.domain.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

@Api(tags = "Product Entity")
@RepositoryRestResource
public interface ProductRepository extends CrudRepository<Product, Long> {

  @ApiOperation("Returns a collection of all products.")
  @Override
  Iterable<Product> findAll();

  @ApiOperation("Returns a product by ID.")
  @ApiParam("The ID of the product to return.")
  @Override
  Optional<Product> findById(Long aLong);

  @ApiOperation("Returns a product by name.")

  @RestResource(path = "byName")
  Optional<Product> findByName(@ApiParam(value = "The name of the product to find.")
                               @Param("name") String name);

  @Override
  @RestResource(exported = false)
  <S extends Product> S save(S s);

  @Override
  @RestResource(exported = false)
  @ApiIgnore
  void delete(Product product);

  @Override
  @RestResource(exported = false)
  @ApiIgnore
  void deleteById(Long aLong);

  @Override
  @RestResource(exported = false)
  @ApiIgnore
  void deleteAll();

  @Override
  @RestResource(exported = false)
  @ApiIgnore
  void deleteAll(Iterable<? extends Product> iterable);
}
