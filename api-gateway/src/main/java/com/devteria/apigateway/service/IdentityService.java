package com.devteria.apigateway.service;

import com.devteria.apigateway.dto.request.IntrospectRequest;
import com.devteria.apigateway.dto.response.ApiResponse;
import com.devteria.apigateway.dto.response.IntrospectResponse;
import com.devteria.apigateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {

    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
        return identityClient.introspect(new IntrospectRequest(token));
    }
}
