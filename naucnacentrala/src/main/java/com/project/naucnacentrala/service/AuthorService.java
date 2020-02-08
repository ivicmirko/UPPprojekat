package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Work;

import java.util.List;

public interface AuthorService {

    public Author findOneById(Long id);
    public Author findOneByUsername(String username);
    public List<Work> findWorksByAuthor(Author author);
    public Author save(Author author);
}
