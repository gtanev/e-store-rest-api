package com.example.restapi.domain.order;

import com.example.restapi.domain.order_product.OrderProduct;
import com.example.restapi.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(hidden = true)
  private Long id;

  @NotNull
  @ApiModelProperty(example = "email@example.com")
  private String customerEmail;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @ApiParam(hidden = true)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderProduct> products = new ArrayList<>();

  public Order() {
  }

  public Order(String customerEmail) {
    this.customerEmail = validateEmail(customerEmail);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = validateEmail(customerEmail);
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<OrderProduct> getProducts() {
    return products;
  }

  public void setProducts(List<OrderProduct> products) {
    products.forEach(p -> addProduct(p.getProduct(), p.getQuantity()));
  }

  @ApiParam(hidden = true)
  public BigDecimal getOrderTotal() {
    return products.stream()
        .map(p -> getProductTotal(p.getQuantity(), p.getPrice()))
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
  }

  public void addProduct(Product product, Integer quantity) {
    products.add(new OrderProduct(this, product, quantity, product.getPrice()));
  }

  private BigDecimal getProductTotal(int productQuantity, BigDecimal productPrice) {
    return productPrice.multiply(BigDecimal.valueOf(productQuantity));
  }

  private String validateEmail(String email) {
    if (!EmailValidator.getInstance().isValid(email))
      throw new IllegalArgumentException("Email address is invalid.");

    return email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Order order = (Order) o;
    return Objects.equals(id, order.getId());
  }

  @Override
  public int hashCode() {
    return 31;
  }
}
