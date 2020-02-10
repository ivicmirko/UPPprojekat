package com.project.naucnacentrala.service;

import com.project.naucnacentrala.dto.NewMagazineDTO;
import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;

import java.util.List;
import java.util.Set;

public interface MagazineService {

    public Magazine saveMagazine(Magazine magazine);
    public Magazine findOneByName(String name);
    public Magazine findOneById(Long id);
    public List<Magazine> findAll();
    public boolean checkNameUniqueness(String name);
    public boolean checkISSNUniqueness(String issn);
    public Magazine addNewMagazine(NewMagazineDTO newMagazine);
    public Magazine addEditorialBoard(NewMagazineDTO magazineDTO);
    public Set<Magazine> findMagazinesByStatus(String magazines);
    public Magazine getMagazineByMainEditor(Editor editor);
    public Magazine getMagazineByEditor(Editor editor);

}
