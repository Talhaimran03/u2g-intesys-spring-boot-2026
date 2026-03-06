package org.u2g.codylab.teamboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findProjectByOwner(User owner, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE p.owner = :user OR m.id = :userId")
    Page<Project> findAllByOwnerOrMember(@Param("user") User user, @Param("userId") Long userId, Pageable pageable);
}
