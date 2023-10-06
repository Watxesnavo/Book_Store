package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);
}
