package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Card;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ColumnMapper.class, UserMapper.class})
public interface CardMapper {


    @Mapping(target = "assignedTo", source = "assignedTo")
    @Mapping(target = "column", source = "column")
    CardApiDTO toResponse(Card card);


    @Mapping(target = "assignedTo.id", source = "assignedToId")
    @Mapping(target = "column.id", source = "columnId")
    @Mapping(target = "id", ignore = true)
    Card toEntity(CreateCardRequestApiDTO request);
}