package com.project.naucnacentrala.serviceIMP;

import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.repository.WorkRepository;
import com.project.naucnacentrala.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkServiceImp implements WorkService {

    @Autowired
    private WorkRepository workRepository;

    @Override
    public Work saveWork(Work work) {
        return this.workRepository.save(work);
    }

    @Override
    public List<Work> findAll() {
        return this.workRepository.findAll();
    }

    @Override
    public Work findById(Long id) {
        return this.workRepository.findOneById(id);
    }

    @Override
    public List<Work> findByAuthor(Author author) {
        return this.workRepository.findAllByAuthor(author);
    }

    @Override
    public List<Work> findByStatus(WorkStatus workStatus) {
        return this.workRepository.findAllByWorkStatus(workStatus);
    }

    @Override
    public List<Work> findByMagazineAndStatus(Magazine magazine, WorkStatus workStatus) {
        return this.workRepository.findAllByMagazineAndWorkStatus(magazine,workStatus);
    }

    @Override
    public void deleteWork(Work work) {
        this.workRepository.delete(work);
    }

    @Override
    public List<Work> findByAuthorAndStatus(Author author, WorkStatus workStatus) {
        return this.workRepository.findAllByAuthorAndWorkStatus(author,workStatus);
    }

    @Override
    public List<Work> findByEditorAndStatus(Editor editor, WorkStatus workStatus) {
//        List<Work> retVal=new ArrayList<>();
//        List<Work> temp=this.workRepository.findAllByWorkStatus(workStatus);
//        for(Work work:temp){
//            System.out.println("Urednik="+work.getEditor().getUsername());
//            if(work.getEditor().getUsername().equals(editor.getUsername())){
//                retVal.add(work);
//            }
//        }
        List<Work> retVal=this.workRepository.findAllByEditorAndWorkStatus(editor,workStatus);
        List<Work> temp=retVal;
        System.out.println("AA="+temp.size());
        return retVal;
    }
}
