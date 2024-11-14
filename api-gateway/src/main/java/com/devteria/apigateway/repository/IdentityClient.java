package com.devteria.apigateway.repository;

import com.devteria.apigateway.dto.request.IntrospectRequest;
import com.devteria.apigateway.dto.response.ApiResponse;
import com.devteria.apigateway.dto.response.IntrospectResponse;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@Repository
public interface IdentityClient {

    @PostExchange(url = "/auth/introspect")
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
