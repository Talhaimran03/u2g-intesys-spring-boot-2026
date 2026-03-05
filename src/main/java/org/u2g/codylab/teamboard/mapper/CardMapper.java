package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Card;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "column", ignore = true)
    CardApiDTO toResponse(Card column);

    @Mapping(target = "assignedTo.id", source = "assignedToId")
    @Mapping(target = "column.id", source = "columnId")
    Card toEntity(CreateCardRequestApiDTO request);
}

