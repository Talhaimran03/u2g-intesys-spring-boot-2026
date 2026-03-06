package org.u2g.codylab.teamboard.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.u2g.codylab.teamboard.dto.ColumnApiDTO;
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.entity.Project;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface ProjectMapper {

    @Mapping(target = "createdBy", source = "owner")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    ProjectResponseApiDTO toApiDTO(Project project);

    @Mapping(target = "members", ignore = true)
    Project toEntity(CreateProjectRequestApiDTO projectApiDTO);

    @Named("localDateTimeToOffsetDateTime")
    static OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(OffsetDateTime.now().getOffset());
    }

}
