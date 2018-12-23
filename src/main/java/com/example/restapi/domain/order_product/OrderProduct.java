package com.example.restapi.domain.order_product;

import com.example.restapi.domain.order.Order;
import com.example.restapi.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@JsonPropertyOrder({"name", "price", "quantity"})
public final class OrderProduct {

  @EmbeddedId
  @ApiModelProperty(hidden = true)
  private OrderProductId id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("orderId")
  @ApiModelProperty(hidden = true)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productId")
  @ApiModelProperty(dataType = "java.lang.String", required = true,
      notes = "product url", example = "https://localhost:8443/api/v1/products/1")
  private Product product;

  @Column
  @ApiParam(hidden = true)
  private BigDecimal price;

  @Column
  @Min(1)
  @ApiModelProperty(required = true, example = "1")
  private Integer quantity;

  public OrderProduct() {
  }

  public OrderProduct(Order order, Product product, Integer quantity, BigDecimal price) {
    this.order = order;
    this.product = product;
    this.quantity = quantity;
    this.price = price;
    this.id = new OrderProductId(order.getId(), product.getId());
  }

  public OrderProductId getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPrice() {
    return price;
  }

  @JsonProperty("name")
  @ApiParam(hidden = true)
  public String getProductName() {
    return product.getName();
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderProduct that = (OrderProduct) o;
    return Objects.equals(order, that.order) &&
        Objects.equals(product, that.product);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order, product);
  }
}
