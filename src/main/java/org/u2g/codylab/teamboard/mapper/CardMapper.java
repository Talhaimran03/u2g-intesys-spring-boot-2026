package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.CardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CardResponseApiDTO;
import org.u2g.codylab.teamboard.entity.Card;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "columnId", ignore = true)
    CardResponseApiDTO toResponse(Card column);

    @Mapping(target = "assignedTo.id", source = "assignedTo")
    @Mapping(target = "column.id", source = "columnId")
    Card toEntity(CardRequestApiDTO request);
}

