package com.project.naucnacentrala.repository;

import java.util.List;

import com.project.naucnacentrala.model.ScienceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScienceAreaRepository extends JpaRepository<ScienceArea, Long> {

	List<ScienceArea> findAll();
	ScienceArea findOneById(Long id);
}
