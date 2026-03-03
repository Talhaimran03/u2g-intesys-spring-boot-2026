package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.u2g.codylab.teamboard.dto.CreateProjectRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ProjectResponseApiDTO;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomForbiddenException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.ProjectMapper;
import org.u2g.codylab.teamboard.repository.ProjectRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act e Assert
        assertThrows(CustomNotFoundException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void shouldCreateProjectSuccessfully() {

        // Arrange
        CreateProjectRequestApiDTO project = new CreateProjectRequestApiDTO()
                .title("Demo")
                .description("Description");

        when(projectMapper.toEntity(any(CreateProjectRequestApiDTO.class)))
                .thenReturn(new Project().setTitle("Demo").setDescription("Description"));
        when(projectRepository.save(any(Project.class)))
                .thenReturn(new Project().setId(1L).setTitle("Demo").setDescription("Description"));
        when(projectMapper.toApiDTO(any(Project.class)))
                .thenReturn(new ProjectResponseApiDTO().id(1L).title("Demo").description("Description"));


        try (MockedStatic<SecurityContextHolder> ignored = mockAuthAndUserRepo()) {
            ProjectResponseApiDTO result = projectService.addProject(project);
            assertNotNull(result);
            verify(projectRepository).save(any(Project.class));
        }
    }

    @Test
    void shouldGetAllProjectsSuccessfully() {
        // Arrange
        Project project = new Project().setTitle("Demo").setDescription("Description");
        Page<Project> projectPage = new PageImpl<>(List.of(project));
        when(projectRepository.findProjectByOwner(any(), any(Pageable.class))).thenReturn(projectPage);
        when(projectMapper.toApiDTO(any())).thenReturn(new ProjectResponseApiDTO().id(1L).title("Demo").description("Description"));

        // Act & Assert
        try (MockedStatic<SecurityContextHolder> ignored = mockAuthAndUserRepo()) {
            Page<ProjectResponseApiDTO> result = projectService.getAllProjects(PageRequest.of(0, 10));
            assertEquals(1, result.getContent().size());
        }
    }

    @Test
    void shouldDeleteProjectSuccessfully() {
        // Arrage
        User user = new User().setId(1L).setUsername("testuser").setPassword("testpassword");
        Project project = new Project().setId(1L).setTitle("Demo").setDescription("Description").setOwner(user);
        when(projectRepository.findById(1L)).thenReturn(Optional.ofNullable(project));
        Mockito.doNothing().when(projectRepository).deleteById(1L);

        // Act & Assert
        try (MockedStatic<SecurityContextHolder> ignored = mockAuthAndUserRepo()) {
            assertDoesNotThrow(() -> projectService.deleteProjectById(1L));
        }
    }

    @Test
    void shouldThrowForbiddenExceptionIfUserIsNotOwner() {
        // Arrage
        User user = new User().setId(2L).setUsername("testuser").setPassword("testpassword");
        Project project = new Project().setId(1L).setTitle("Demo").setDescription("Description").setOwner(user);
        when(projectRepository.findById(1L)).thenReturn(Optional.ofNullable(project));

        // Act & Assert
        try (MockedStatic<SecurityContextHolder> ignored = mockAuthAndUserRepo()) {
            assertThrows(CustomForbiddenException.class, () -> projectService.deleteProjectById(1L));
        }
    }

    MockedStatic<SecurityContextHolder> mockAuthAndUserRepo() {

        String username = "testuser";

        User user = new User().setId(1L).setUsername(username).setPassword("testpassword");

        MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        return mockedSecurityContextHolder;
    }

}
