package com.example.restapi.domain.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Api(tags = "Order Entity")
@RepositoryRestResource
public interface OrderRepository extends CrudRepository<Order, Long> {

  @Override
  @RestResource(exported = false)
  <S extends Order> S save(S s);

  @ApiOperation("Returns a collection of all orders made within a specified time period.")
  @RestResource(path = "betweenTime", rel = "timePeriodSearch")
  Optional<List<Order>> findAllByCreatedAtBetween(
      @ApiParam(value = "The creation time to filter orders from, specified in <b>" + dateFormat + "</b> format.")
      @Param("from") @DateTimeFormat(pattern = dateFormat) LocalDateTime publicationTimeStart,
      @ApiParam(value = "The creation time to filter orders to, specified in <b>" + dateFormat + "</b> format.")
      @Param("to") @DateTimeFormat(pattern = dateFormat) LocalDateTime publicationTimeEnd
  );

  @ApiOperation("Returns a collection of all orders.")
  @Override
  Iterable<Order> findAll();

  @ApiOperation("Returns an order by ID.")
  @ApiParam("The ID of the order to return.")
  @Override
  Optional<Order> findById(Long aLong);

  @ApiOperation("Deletes an order by ID.")
  @ApiParam("The ID of the order to delete.")
  @Override
  void deleteById(Long aLong);

  String dateFormat = "yyyy.MM.dd HH:mm";
}
