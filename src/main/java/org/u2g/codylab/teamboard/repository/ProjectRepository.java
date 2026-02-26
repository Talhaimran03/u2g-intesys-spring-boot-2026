package org.u2g.codylab.teamboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findProjectByOwner(User owner);
}
