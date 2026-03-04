package org.u2g.codylab.teamboard.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomAnauthorizedException;
import org.u2g.codylab.teamboard.exception.CustomForbiddenException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.ProjectMapper;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Transactional
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectMapper projectMapper, ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Page<ProjectResponseApiDTO> getAllProjects(Pageable pageData) {

        log.info("Getting all projects: {}", pageData);

        User loggedInUser = getLoggedUser();

        Page<Project> projectPage = projectRepository.findProjectByOwner(loggedInUser, pageData);

        List<ProjectResponseApiDTO> dtos = projectPage.getContent().stream()
            .map(project -> {
                ProjectResponseApiDTO dto = projectMapper.toApiDTO(project);
                long cardsCount = project.getColumns() == null ? 0L : (long) project.getColumns().size();
                dto.setCardsNumber(BigDecimal.valueOf(cardsCount));
                return dto;
            })
            .toList();

        Page<ProjectResponseApiDTO> responseApiDTOS = new PageImpl<>(dtos, projectPage.getPageable(), projectPage.getTotalElements());

        log.info("Retrieved {} projects", responseApiDTOS.getContent().size());
        return responseApiDTOS;

    }

    public ProjectResponseApiDTO addProject(CreateProjectRequestApiDTO project) {
        log.info("Adding project: {}", project);

        Project projectEntity = projectMapper.toEntity(project);
        if (project.getMembers() != null && !project.getMembers().isEmpty()) {
            List<User> members = project.getMembers().stream()
                .map(userId -> userRepository.findById(userId)
                    .orElseThrow(() -> new CustomNotFoundException("Member not found with id: " + userId)))
                .toList();
            projectEntity.setMembers(members);
        }
        projectEntity.setOwner(getLoggedUser());
        projectEntity.setCreatedAt(LocalDateTime.now());
        projectEntity.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(projectEntity);
        ProjectResponseApiDTO projectResponseApiDTO = projectMapper.toApiDTO(savedProject);

        log.info("Project added: {}", projectResponseApiDTO);
        return projectResponseApiDTO;
    }

    public Void deleteProjectById(Long id) {

        log.info("Deleting project with id: {}", id);

        User loggedInUser = getLoggedUser();

        Project project = projectRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + id));

        if (project.getOwner().getId().equals(loggedInUser.getId())) {
            projectRepository.deleteById(id);
            log.info("Project deleted with id: {}", id);
            return null;
        }

        throw new CustomForbiddenException("User not authorized to delete this project");
    }

    public ProjectResponseApiDTO getProjectById(Long id) {
        log.info("Getting project with id: {}", id);
        ProjectResponseApiDTO projectResponseApiDTO = projectRepository.findById(id)
                .map(projectMapper::toApiDTO)
                .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + id));
        log.info("Retrieved project: {}", projectResponseApiDTO);
        return projectResponseApiDTO;
    }


    public ProjectResponseApiDTO updateProjectById(Long id, UpdateProjectRequestApiDTO projectRequestApiDTO) {

        log.info("Updating project with id: {}", id);

        Project project = projectRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + id));

        project.setTitle(projectRequestApiDTO.getTitle());
        project.setDescription(projectRequestApiDTO.getDescription());
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);
        ProjectResponseApiDTO projectResponseApiDTO = projectMapper.toApiDTO(updatedProject);

        log.info("Updated project: {}", projectResponseApiDTO);
        return projectResponseApiDTO;
    }

    private User getLoggedUser() {
        log.info("Getting logged user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = Objects.requireNonNull(authentication).getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomAnauthorizedException("User not found with username: " + username));
        log.info("Retrieved user correctly");

        return user;
    }
}
