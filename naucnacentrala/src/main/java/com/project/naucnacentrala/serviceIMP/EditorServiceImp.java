package com.project.naucnacentrala.serviceIMP;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.repository.EditorRepository;
import com.project.naucnacentrala.repository.MagazineRepository;
import com.project.naucnacentrala.repository.SystemUserRepository;
import com.project.naucnacentrala.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EditorServiceImp implements EditorService {


    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private EditorRepository editorRepository;

    @Override
    public Set<Editor> findFreeEditors() {
        return this.editorRepository.findEditorsByMagazineIsNull();
    }

    @Override
    public Editor findEditorByUsername(String username) {
        return this.editorRepository.findOneByUsername(username);
    }

    @Override
    public List<Editor> findByMagazineAndScienceArea(Magazine magazine, ScienceArea scienceArea) {
        return this.editorRepository.findAllByMagazineAndScienceAreasIsContaining(magazine,scienceArea);
    }
}
