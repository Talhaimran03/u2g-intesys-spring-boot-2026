package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UserResponseApiDTO;
import org.u2g.codylab.teamboard.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseApiDTO toApiDTO(User user);

    User toEntity(RegisterRequestApiDTO dto);
}