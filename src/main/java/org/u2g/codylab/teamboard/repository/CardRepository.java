package org.u2g.codylab.teamboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.u2g.codylab.teamboard.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
