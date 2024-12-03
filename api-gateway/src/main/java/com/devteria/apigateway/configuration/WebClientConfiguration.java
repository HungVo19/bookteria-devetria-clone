package com.devteria.apigateway.configuration;

import com.devteria.apigateway.repository.IdentityClient;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfiguration {

  //Build web client
  @Bean
  WebClient webClient() {
    return WebClient.builder()
        .baseUrl("http://localhost:8080/identity")
        .build();
  }

  // Config CORS
  // Only need to config only once at API Gateway service
  @Bean
  CorsWebFilter corsWebFilter() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
        new UrlBasedCorsConfigurationSource();

    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

    return new CorsWebFilter(urlBasedCorsConfigurationSource);
  }

  @Bean
  IdentityClient identityClient(WebClient webClient) {

    //Khi tạo request thì proxy sẽ tạo webclient gọi tới url tương ứng
    HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
        .builderFor(WebClientAdapter.create(webClient)).build();

    return httpServiceProxyFactory.createClient(IdentityClient.class);
  }

}
