package com.devteria.identity.mapper;

import com.devteria.identity.dto.request.ProfileCreationRequest;
import com.devteria.identity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

  @Mapping(source = "id", target = "userId")
  ProfileCreationRequest toProfileCreationRequest(User request);
}
