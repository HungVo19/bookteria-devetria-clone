package com.devteria.apigateway.configuration;

import com.devteria.apigateway.dto.response.ApiResponse;
import com.devteria.apigateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

  IdentityService identityService;
  ObjectMapper objectMapper;

  @NonFinal
  String[] publicEndpoints = {"/identity/auth/.*", "/identity/users/registration"};

  @Value("${app.api-prefix}")
  @NonFinal
  private String apiPrefix;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (isPublicEndpoint(exchange.getRequest())) {
      return chain.filter(exchange);
    }

    log.info("Enter authentication filer...");
    // Get token from authorization header
    List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
    if (CollectionUtils.isEmpty(authHeader)) {
      return unauthenticated(exchange.getResponse());
    }

    String token = authHeader.getFirst().replace("Bearer", "");
    log.info("Authentication token: {}", token);

    // Verify token
    identityService.introspect(token).subscribe(introspectResponseApiResponse ->
        log.info("Result: {}", introspectResponseApiResponse.getResult().isValid()));

    return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
      if (introspectResponseApiResponse.getResult().isValid()) {
        // Delegate to identity service
        return chain.filter(exchange);
      }
      return unauthenticated(exchange.getResponse());
    }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
  }

  // the smaller the number, the higher the order
  @Override
  public int getOrder() {
    return -1;
  }

  // check if endpoint is public, then return true
  private boolean isPublicEndpoint(ServerHttpRequest request) {
    return Arrays.stream(publicEndpoints)
        .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
  }

  Mono<Void> unauthenticated(ServerHttpResponse response) {
    ApiResponse<?> apiResponse = ApiResponse.builder()
        .code(1401)
        .message("Unauthenticated")
        .build();
    String body = null;
    try {
      body = objectMapper.writeValueAsString(apiResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    return response.writeWith(
        Mono.just(response.bufferFactory().wrap(body.getBytes())));
  }
}
