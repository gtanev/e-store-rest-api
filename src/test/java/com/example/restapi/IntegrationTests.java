package com.example.restapi;

import com.example.restapi.domain.order.Order;
import com.example.restapi.domain.order.OrderRepository;
import com.example.restapi.domain.order_product.OrderProduct;
import com.example.restapi.domain.product.Product;
import com.example.restapi.domain.product.ProductRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTests {

  @Autowired
  private TestRestTemplate template;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String ordersURL = "/orders";
  private static final String productsURL = "/products";

  private static List<Product> products = new ArrayList<>();
  private static List<Order> orders = new ArrayList<>();


  @BeforeAll
  public void init() {
    template = template.withBasicAuth("admin", "pass");
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    Product p1 = new Product("Pen", BigDecimal.valueOf(10));
    Product p2 = new Product("Ink", BigDecimal.valueOf(4.5));
    Product p3 = new Product("Eraser", BigDecimal.valueOf(2.75));

    Order o1 = new Order("george.tanev@valid-domain.com");
    Order o2 = new Order("example@domain.com");

    products.addAll(Arrays.asList(p1, p2, p3));
    orders.add(o1);
    orders.add(o2);

    productRepository.saveAll(products);
    orderRepository.saveAll(orders);
  }

  @AfterAll
  public void testUnauthorizedRequests() {
    template = template.withBasicAuth("admin", "incorrectPassword");

    assertEquals(HttpStatus.UNAUTHORIZED, template.getForEntity(productsURL, String.class).getStatusCode());
    assertEquals(HttpStatus.UNAUTHORIZED, template.getForEntity(ordersURL, String.class).getStatusCode());
  }

  @Nested
  class Products {
    @Test
    public void testGet_findAllProducts() {
      ResponseEntity<Resources> response = template.getForEntity(productsURL, Resources.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(MediaTypes.HAL_JSON_UTF8, response.getHeaders().getContentType());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getContent());
      assertFalse(response.getBody().getContent().isEmpty());
    }

    @Test
    public void testGet_findProductById() {
      final String resourceUrl = productsURL + "/" + 1;

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response =
          template.exchange(resourceUrl, HttpMethod.GET, null, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());

      Resource<Product> resource = response.getBody();

      assertNotNull(resource);
      assertTrue(resource.hasLinks());
      assertTrue(resource.hasLink("self"));
      assertEquals(products.get(0), resource.getContent());
    }

    @Test
    public void testPost_createProduct() {
      Product product = new Product("Ruler", BigDecimal.valueOf(12));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response
          = template.exchange(productsURL, HttpMethod.POST, entity, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());

      Resource<Product> resource = response.getBody();

      assertNotNull(resource);
      assertEquals(product, resource.getContent());
      assertTrue(resource.hasLinks());
      assertTrue(resource.hasLink("self"));
    }

    @Test
    public void testPost_createProduct_NoPrice() {
      Product product = new Product("Ruler", null);

      HttpEntity<Product> entity = new HttpEntity<>(product);

      products.add(product);

      ResponseEntity<String> response
          = template.exchange(productsURL, HttpMethod.POST, entity, String.class);

      assertNotNull(response);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNull(response.getBody());
    }

    @Test
    public void testPost_createProduct_NoName() {
      Product product = new Product(null, BigDecimal.valueOf(5));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      products.add(product);

      ResponseEntity<String> response
          = template.exchange(productsURL, HttpMethod.POST, entity, String.class);

      assertNotNull(response);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNull(response.getBody());
    }

    @Test
    public void testPost_createProduct_Conflict() {
      Product product = new Product("Pen", BigDecimal.valueOf(12));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response
          = template.exchange(productsURL, HttpMethod.POST, entity, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
      assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    public void testPatch_updateProduct() {

      Product product = new Product("Pen", BigDecimal.valueOf(15));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      final String resourceUrl = productsURL + "/" + 1;

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response =
          template.exchange(resourceUrl, HttpMethod.PATCH, entity, responseType);

      Resource<Product> resource = response.getBody();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(resource);
      assertEquals(product.getPrice(), resource.getContent().getPrice());
    }

    @Test
    public void testPatch_updateProduct_BadRequest() {

      Product product = new Product("Pen", null);

      HttpEntity<Product> entity = new HttpEntity<>(product);

      final String resourceUrl = productsURL + "/" + 1;

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response =
          template.exchange(resourceUrl, HttpMethod.PATCH, entity, responseType);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testPatch_updateProduct_NoContent() {

      Product product = new Product("Pen", BigDecimal.valueOf(25));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      final String resourceUrl = productsURL + "/" + Integer.MAX_VALUE;

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response =
          template.exchange(resourceUrl, HttpMethod.PATCH, entity, responseType);

      assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testPut_NotAllowed() {
      Product product = new Product("Pen", BigDecimal.valueOf(25));

      HttpEntity<Product> entity = new HttpEntity<>(product);

      final String resourceUrl = productsURL + "/" + 1;

      ParameterizedTypeReference<Resource<Product>> responseType
          = new ParameterizedTypeReference<Resource<Product>>() {
      };

      ResponseEntity<Resource<Product>> response =
          template.exchange(resourceUrl, HttpMethod.PUT, entity, responseType);

      assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
      assertTrue(response.getHeaders().containsKey(HttpHeaders.ALLOW));
    }

    @Test
    public void testDelete_deleteProduct_NotAllowed() {
      final String resourceUrl = productsURL + "/" + products.size();

      final ResponseEntity<Void> response = template.exchange(resourceUrl, HttpMethod.DELETE, null, Void.class);

      assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

  }

  @Nested
  class Orders {
    @Test
    public void testGet_findAllOrders() {
      ResponseEntity<Resources> response = template.getForEntity(ordersURL, Resources.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(MediaTypes.HAL_JSON_UTF8, response.getHeaders().getContentType());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getContent());
      assertFalse(response.getBody().getContent().isEmpty());
    }

    @Test
    public void testGet_findAllOrdersByCreationTime() {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

      final String start = LocalDateTime.now().minusDays(5).format(formatter);
      final String end = LocalDateTime.now().plusDays(5).format(formatter);

      final String resourceUrl = ordersURL + "/search/betweenTime?from=" + start + "&to=" + end;

      ResponseEntity<Resources> response = template.getForEntity(resourceUrl, Resources.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(MediaTypes.HAL_JSON_UTF8, response.getHeaders().getContentType());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getContent());
      assertFalse(response.getBody().getContent().isEmpty());
    }

    @Test
    public void testGet_findOrderById() {
      final String resourceUrl = ordersURL + "/" + 1;

      ParameterizedTypeReference<Resource<Order>> responseType
          = new ParameterizedTypeReference<Resource<Order>>() {
      };

      ResponseEntity<Resource<Order>> response =
          template.exchange(resourceUrl, HttpMethod.GET, null, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());

      Resource<Order> resource = response.getBody();

      assertNotNull(resource);
      assertTrue(resource.hasLinks());
      assertTrue(resource.hasLink("self"));
      assertEquals(orders.get(0).getCustomerEmail(), resource.getContent().getCustomerEmail());
    }


    @Test
    public void testPost_createOrder() {
      final Order order = new Order("george.tanev@valid-domain.com");
      final HttpEntity<Resource<Order>> entity = new HttpEntity<>(new Resource<>(order));

      ParameterizedTypeReference<Resource<Order>> responseType
          = new ParameterizedTypeReference<Resource<Order>>() {
      };

      ResponseEntity<Resource<Order>> response
          = template.exchange(ordersURL, HttpMethod.POST, entity, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      Resource<Order> resource = response.getBody();
      assertNotNull(resource);
      assertTrue(resource.hasLinks());
      assertTrue(resource.hasLink("self"));
    }

    @Test
    public void testPost_createOrder_WithProducts() throws IOException, JSONException {
      final Order order = new Order("george.tanev@valid-domain.com");

      order.addProduct(productRepository.findById(1L).orElseThrow(ResourceNotFoundException::new), 4);
      order.addProduct(productRepository.findById(2L).orElseThrow(ResourceNotFoundException::new), 2);

      final String requestBody = orderToJsonString(order);
      final HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaTypes.HAL_JSON);

      final HttpEntity<?> entity = new HttpEntity<>(requestBody, requestHeaders);

      ResponseEntity<String> response
          = template.exchange(ordersURL, HttpMethod.POST, entity, String.class);

      final Order responseOrder = jsonStringToOrder(response.getBody());

      assertNotNull(response);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(responseOrder);
      assertEquals(order.getCustomerEmail(), responseOrder.getCustomerEmail());
      assertEquals(order.getProducts(), responseOrder.getProducts());
      assertEquals(order.getOrderTotal(), responseOrder.getOrderTotal());
    }

    @Test
    public void testPost_createOrder_BadRequest() {
      final HttpEntity<?> entity = new HttpEntity<>(null);

      ParameterizedTypeReference<Resource<Order>> responseType
          = new ParameterizedTypeReference<Resource<Order>>() {
      };

      ResponseEntity<Resource<Order>> response
          = template.exchange(ordersURL, HttpMethod.POST, entity, responseType);

      assertNotNull(response);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDelete_deleteOrder() {
      final String resourceUrl = ordersURL + "/" + orders.size();

      ResponseEntity<Void> response = template.exchange(resourceUrl, HttpMethod.DELETE, null, Void.class);

      assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDelete_deleteOrder_NotFound() {
      final String resourceUrl = ordersURL + "/" + Integer.MAX_VALUE;

      ResponseEntity<Void> response = template.exchange(resourceUrl, HttpMethod.DELETE, null, Void.class);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private String orderToJsonString(final Order order) throws JsonProcessingException, JSONException {
      final JSONObject orderJSON = new JSONObject(objectMapper.writeValueAsString(order));

      JSONArray products = orderJSON.getJSONArray("products");

      while (products.length() > 0) {
        products.remove(0);
      }

      for (OrderProduct p : order.getProducts()) {
        String productURI = template.getRootUri() + productsURL + "/" + p.getProduct().getId();
        products.put(new JSONObject()
            .put("product", productURI)
            .put("quantity", p.getQuantity()));
      }

      return orderJSON.toString(4).replace("\\", "");
    }

    private Order jsonStringToOrder(final String content) throws JSONException, IOException {
      JSONObject orderJSON = new JSONObject(content);
      JSONArray productsJSON = orderJSON.getJSONArray("products");

      orderJSON.remove("products");

      final Order order = objectMapper.readValue(orderJSON.toString(), Order.class);

      for (int i = 0; i < productsJSON.length(); i++) {
        JSONObject p = productsJSON.getJSONObject(i);
        Optional<Product> product = productRepository.findByName(p.get("name").toString());
        order.addProduct(
            product.orElseThrow(ResourceNotFoundException::new),
            Integer.valueOf(p.get("quantity").toString())
        );
      }

      return order;
    }
  }
}
