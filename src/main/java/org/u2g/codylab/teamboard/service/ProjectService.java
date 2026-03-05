package org.u2g.codylab.teamboard.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomForbiddenException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.ColumnMapper;
import org.u2g.codylab.teamboard.mapper.ProjectMapper;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;
import org.u2g.codylab.teamboard.util.AuthUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ColumnMapper columnMapper;
    private final UserMapper userMapper;

    public ProjectService(ProjectMapper projectMapper, ProjectRepository projectRepository, UserRepository userRepository, ColumnMapper columnMapper, UserMapper userMapper) {
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.columnMapper = columnMapper;
        this.userMapper = userMapper;
    }

    public Page<ProjectResponseApiDTO> getAllProjects(Pageable pageData) {

        log.info("Getting all projects: {}", pageData);

        User loggedInUser = AuthUtil.getLoggedUser(userRepository);

        Page<Project> projects = projectRepository.findAllByOwnerOrMember(loggedInUser, loggedInUser.getId(), pageData);
        List<ProjectResponseApiDTO> dtos = projects.getContent().stream()
            .map(project -> {
                ProjectResponseApiDTO dto = projectMapper.toApiDTO(project);
                long cardsCount = project.getColumns() == null ? 0L : (long) project.getColumns().size();
                dto.setCardsNumber(BigDecimal.valueOf(cardsCount));
                return dto;
            })
            .toList();

        Page<ProjectResponseApiDTO> responseApiDTOS = new PageImpl<>(dtos, pageData, projects.getTotalElements());
        log.info("Retrieved {} projects", responseApiDTOS.getContent().size());
        return responseApiDTOS;

    }

    public ProjectResponseApiDTO addProject(CreateProjectRequestApiDTO project) {
        log.info("Adding project: {}", project);

        Project projectEntity = projectMapper.toEntity(project);
        if (projectEntity.getColumns() != null)
            projectEntity.getColumns().forEach(column -> column.setProject(projectEntity));
        if (project.getMembers() != null && !project.getMembers().isEmpty()) {
            List<User> members = project.getMembers().stream()
                .map(userId -> userRepository.findById(userId)
                    .orElseThrow(() -> new CustomNotFoundException("Member not found with id: " + userId)))
                .toList();
            projectEntity.setMembers(members);
        }
        projectEntity.setOwner(AuthUtil.getLoggedUser(userRepository));
        projectEntity.setCreatedAt(LocalDateTime.now());
        projectEntity.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(projectEntity);
        ProjectResponseApiDTO projectResponseApiDTO = projectMapper.toApiDTO(savedProject);

        log.info("Project added: {}", projectResponseApiDTO);
        return projectResponseApiDTO;
    }

    public Void deleteProjectById(Long id) {

        log.info("Deleting project with id: {}", id);

        User loggedInUser = AuthUtil.getLoggedUser(userRepository);

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

    public List<UserApiDTO> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + projectId));
        return project.getMembers().stream().map(userMapper::toApiDTO).toList();
    }

    public void addProjectMembers(Long projectId, List<Long> userIds) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + projectId));

        userIds.forEach(userId -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("User not found with id: " + userId));
            if (!project.getMembers().contains(user)) {
                project.getMembers().add(user);
            } else {
                throw new CustomIllegalArgumentException("User already exists in project");
            }
        });

        projectRepository.save(project);
    }

    public void removeProjectMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + projectId));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomNotFoundException("User not found with id: " + userId));

        if (project.getMembers().contains(user)) {
            project.getMembers().remove(user);
            projectRepository.save(project);
        } else {
            throw new CustomIllegalArgumentException("User not found in project");
        }
    }

    public List<ColumnResponseApiDTO> getProjectColumns(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new CustomNotFoundException("Project not found with id: " + projectId));
        return project.getColumns().stream().map(columnMapper::toResponse).toList();
    }
}
