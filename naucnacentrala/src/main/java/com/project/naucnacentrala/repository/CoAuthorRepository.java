package com.project.naucnacentrala.repository;

import com.project.naucnacentrala.model.CoAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoAuthorRepository extends JpaRepository<CoAuthor, Long> {
}
