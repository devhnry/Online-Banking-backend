package org.henry.onlinebankingsystemp.repository;

import org.henry.onlinebankingsystemp.entity.IdGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdGenRepository extends JpaRepository<IdGen, Long> {
}
