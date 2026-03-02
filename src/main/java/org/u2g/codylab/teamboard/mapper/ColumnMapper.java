package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Column;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ColumnMapper {

    @Mapping(target = "projectId", source = "project.id")
    ColumnResponseApiDTO toResponse(Column column);

    Column toEntity(CreateColumnRequestApiDTO request);
}

