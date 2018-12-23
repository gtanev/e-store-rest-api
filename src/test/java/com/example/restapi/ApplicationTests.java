package com.example.restapi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {
  private ConfigurableApplicationContext context;

  @Test
  public void applicationContextTest() {
    context = SpringApplication.run(Application.class);
  }

  @Test
  public void applicationContextLoaded() {
  }

  @AfterAll
  public void destroy() {
    SpringApplication.exit(context);
  }

}
