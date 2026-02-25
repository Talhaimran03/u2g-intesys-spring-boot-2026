package org.u2g.codylab.teamboard.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.u2g.codylab.teamboard.dto.ProjectApiDTO;
import org.u2g.codylab.teamboard.dto.UserApiDTO;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

    ProjectApiDTO toApiDTO(Project project);

    Project toEntity(ProjectApiDTO projectApiDTO);
}
