package org.u2g.codylab.teamboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findProjectByOwner(User owner, Pageable pageable);
    Page<Project> findByOwnerAndTitleContainingIgnoreCase(User owner, String title, Pageable pageable);
}
