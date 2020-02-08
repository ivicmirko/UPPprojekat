package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.model.WorkStatus;

import java.util.List;

public interface WorkService {

    public Work saveWork(Work work);
    public List<Work> findAll();
    public Work findById(Long id);
    public List<Work> findByAuthor(Author author);
    public List<Work> findByStatus(WorkStatus workStatus);
    public List<Work> findByMagazineAndStatus(Magazine magazine, WorkStatus workStatus);
    public void deleteWork(Work work);
    public List<Work> findByAuthorAndStatus(Author author, WorkStatus workStatus);
}
