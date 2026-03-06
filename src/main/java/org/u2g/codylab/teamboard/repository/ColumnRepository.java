package org.u2g.codylab.teamboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.u2g.codylab.teamboard.entity.Column;

import java.util.List;

public interface ColumnRepository extends JpaRepository<Column, Long> {
    List<Column> findByProjectId(Long projectId);
}

