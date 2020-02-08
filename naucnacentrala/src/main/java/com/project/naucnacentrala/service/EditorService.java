package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;

import java.util.List;
import java.util.Set;

public interface EditorService {

    public Set<Editor> findFreeEditors();
    public Editor findEditorByUsername(String username);
    public List<Editor> findByMagazineAndScienceArea(Magazine magazine, ScienceArea scienceArea);
}
