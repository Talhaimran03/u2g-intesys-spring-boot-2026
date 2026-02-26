package org.u2g.codylab.teamboard.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.ProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.mapper.ProjectMapper;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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

    public List<ProjectResponseApiDTO> getAllProjects() {

        User loggedInUser = getLoggedUser();

        List<Project> projectResponseApiDTOS = projectRepository.findProjectByOwner(loggedInUser);
        return projectResponseApiDTOS.stream().map(projectMapper::toApiDTO).toList();
    }

    public ProjectResponseApiDTO addProject(ProjectRequestApiDTO project) {
        Project projectEntity = projectMapper.toEntity(project);
        projectEntity.setOwner(getLoggedUser());
        Project savedProject = projectRepository.save(projectEntity);
        return projectMapper.toApiDTO(savedProject);
    }

    public Void deleteProjectById(Long id) {
        User loggedInUser = getLoggedUser();

        Project project = projectRepository.findById(id).orElse(null);

        if (project == null) {
            throw new RuntimeException("User not found or project not found");
        }

        if (project.getOwner().getId().equals(loggedInUser.getId())) {
            projectRepository.deleteById(id);
            return null;
        } else {
            throw new RuntimeException("User not authorized to delete this project");
        }
    }

    public ProjectResponseApiDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toApiDTO)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }


    public ProjectResponseApiDTO updateProjectById(Long id, ProjectRequestApiDTO projectRequestApiDTO) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        project.setTitle(projectRequestApiDTO.getTitle());
        project.setDescription(projectRequestApiDTO.getDescription());
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toApiDTO(updatedProject);
    }

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

//    public Project getOneProjects(long id){
//        List<Project> projects = getAllProjects();
//        Optional<Project> project = projects.stream().filter(p -> p.getId() == id).findFirst();
//        return project.orElse(null);
//    }
}
