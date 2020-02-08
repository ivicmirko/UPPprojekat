package com.project.naucnacentrala.repository;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.model.WorkStatus;
import com.project.naucnacentrala.service.WorkService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    Work findOneById(Long id);
    List<Work> findAll();
    List<Work> findAllByWorkStatus(WorkStatus workStatus);
    List<Work> findAllByAuthor(Author author);
    List<Work> findAllByMagazine(Magazine magazine);
    List<Work> findAllByMagazineAndWorkStatus(Magazine magazine,WorkStatus workStatus);
    List<Work> findAllByAuthorAndWorkStatus(Author author, WorkStatus workStatus);
}
