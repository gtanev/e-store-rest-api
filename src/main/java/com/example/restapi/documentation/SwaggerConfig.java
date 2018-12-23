package com.example.restapi.documentation;

import com.example.restapi.Application;
import com.example.restapi.domain.order.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Configuration
@EnableSwagger2WebMvc
@Import({SpringDataRestConfiguration.class, BeanValidatorPluginsConfiguration.class})
@ComponentScan(basePackageClasses = {Application.class})
public class SwaggerConfig {

  private static final Set<String> contentTypes =
      new HashSet<>(Collections.singletonList(MediaTypes.HAL_JSON_VALUE));

  @Bean
  public Docket apiDocket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("/api/v1")
        .select()
        .apis(customRequestHandlers())
        .paths(PathSelectors.ant("/api/v1/products/**")
            .or(PathSelectors.ant("/api/v1/orders/**")))
        .build()
        .tags(new Tag("Order Entity", "<div align='right'>Endpoint for managing orders</div>"),
            new Tag("Product Entity", "<div align='right'>Endpoint for managing products</div>"))
        .consumes(contentTypes)
        .produces(contentTypes)
        .apiInfo(getApiInfo());
  }


  private Predicate<RequestHandler> customRequestHandlers() {
    return input -> {
      Set<RequestMethod> methods = input.supportedMethods();
      String resourceType = input.getReturnType().getTypeBindings().toString();
      return !resourceType.contains(Order.class.getTypeName())
          || !(methods.contains(RequestMethod.PATCH) || methods.contains(RequestMethod.PUT));
    };
  }

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder()
        .title("eStore")
        .description("Spring Boot REST API Example")
        .version("1.0.0")
        .contact(new Contact("George Tanev", "http://github.com/gtanev", "georgetjazz@gmail.com"))
        .license("MIT License")
        .licenseUrl("https://opensource.org/licenses/MIT")
        .build();
  }
}
