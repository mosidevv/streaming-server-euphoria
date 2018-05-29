package de.digitalcollections.streaming.euphoria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringConfigSecurity extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.user.name}")
  private String actuatorUsername;

  @Value("${spring.security.user.password}")
  private String actuatorPassword;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().passwordEncoder(passwordEncoderDummy()).withUser(User.withUsername(actuatorUsername).password(actuatorPassword).roles("ACTUATOR"));
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(EndpointRequest.to("health", "info", "version")).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
            .antMatchers("/**").permitAll()
            .and()
            .httpBasic();
  }

  private PasswordEncoder passwordEncoderDummy() {
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
      }
    };
  }
}
