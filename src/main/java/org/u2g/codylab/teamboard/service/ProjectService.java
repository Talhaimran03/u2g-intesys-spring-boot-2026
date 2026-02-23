package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.entity.Project;

import java.util.List;

@Service
public class ProjectService {

    public List<Project> getAllProjects() {
        return List.of(new Project(1L, "Fist object", "Demo description"));
    }
}
