package com.project.naucnacentrala.repository;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {

    List<Editor> findAll();
    Editor findOneById(Long id);
    Editor findOneByUsername(String name);
    Set<Editor> findEditorsByMagazineIsNull();
    List<Editor> findAllByMagazineAndScienceAreasIsContaining(Magazine magazine, ScienceArea scienceArea);
//find all slobodne urednike
//@Query(value="SELECT * FROM systmeuser u, user_authority ua, authority a WHERE u.id=ua.user_id and a.id=ua.authority_id and a.name='editor' and u.magazine_id=null ",nativeQuery = true)
//Set<SystemUser> findFreeEditors();

}
