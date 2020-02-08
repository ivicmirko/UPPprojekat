package com.project.naucnacentrala.repository;

import com.project.naucnacentrala.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    List<Authority> findAll();
    Authority findAuthorityByName(String name);
}
