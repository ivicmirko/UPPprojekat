package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.ScienceArea;

import java.util.List;

public interface ScienceAreaService {

    public List<ScienceArea> findAll();

    public ScienceArea findOneById(long id);
}
