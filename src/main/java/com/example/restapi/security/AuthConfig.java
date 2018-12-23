package com.example.restapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {

  @Value("${auth.user.name:}")
  private String userName;

  @Value("${auth.user.password:}")
  private String userPasswordHashed;

  @Value("${auth.enabled:true}")
  private boolean securityEnabled;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    if (securityEnabled)
      http.csrf().disable().authorizeRequests().anyRequest().authenticated()
          .and().logout()
          .and().httpBasic()
          .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    if (securityEnabled)
      auth.inMemoryAuthentication()
          .passwordEncoder(passwordEncoder())
          .withUser(userName)
          .password(userPasswordHashed)
          .roles("ADMIN");
  }

  @Override
  public void configure(WebSecurity web) {
    if (!securityEnabled) web.ignoring().antMatchers("/**");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
