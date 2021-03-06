package com.project.naucnacentrala.serviceIMP;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.repository.AuthorRepository;
import com.project.naucnacentrala.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImp implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Author findOneById(Long id) {
        return this.findOneById(id);
    }

    @Override
    public Author findOneByUsername(String username) {
        return this.authorRepository.findOneByUsername(username);
    }

    @Override
    public List<Work> findWorksByAuthor(Author author) {
        return null;
    }

    @Override
    public Author save(Author author) {
        return this.authorRepository.save(author);
    }
}
