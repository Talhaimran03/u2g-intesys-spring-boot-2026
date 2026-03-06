package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.ColumnApiDTO;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Column;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CardMapper.class)
public interface ColumnMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "cards", source = "cards")
    ColumnResponseApiDTO toResponse(Column column);

    Column toEntity(CreateColumnRequestApiDTO request);

    Column toEntity(ColumnApiDTO request);
}

