package org.henry.onlinebankingsystemp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdGenRepository extends JpaRepository<IdGen, Long> {
}
