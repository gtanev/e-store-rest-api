package com.example.restapi.domain.order_product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public final class OrderProductId implements Serializable {

  @Column
  private Long orderId;
  @Column
  private Long productId;

  private OrderProductId() {
  }

  public OrderProductId(Long orderId, Long productId) {
    this.orderId = orderId;
    this.productId = productId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderProductId that = (OrderProductId) o;
    return Objects.equals(orderId, that.orderId) &&
        Objects.equals(productId, that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderId, productId);
  }
}

