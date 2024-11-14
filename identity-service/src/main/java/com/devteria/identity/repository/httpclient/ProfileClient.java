package com.devteria.identity.repository.httpclient;

import com.devteria.identity.dto.request.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//Either name or value is required for service registry like Eureka
@FeignClient(value = "profile-service", url = "${app.service.profile.url}")
public interface ProfileClient {

  @PostMapping(value = "/internal/users")
  Object createProfile(@RequestBody ProfileCreationRequest request);
}
