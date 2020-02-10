package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.EditorService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SetEditorOfScienceAreaHandler implements JavaDelegate {

    @Autowired
    EditorService editorService;

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U DODELU UREDNIKA NAUCNE OBLASTI");

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=new Work();
        try{
            work=this.workService.findById(workId);
        }catch (NullPointerException e) {
            System.out.println("Ne moze da pronadje rad");
            e.printStackTrace();
        }

        Magazine magazine=new Magazine();
        try{
            magazine=work.getMagazine();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        ScienceArea scienceArea=work.getScienceArea();
        List<Editor> editors=this.editorService.findByMagazineAndScienceArea(magazine,scienceArea);

        Editor editor=editors.get(0);
        for(Editor e:editors){
            if(!e.getUsername().equals(magazine.getMainEditor().getUsername())){
                editor=e;
            }
        }
        for(Editor e:editors){
            if(e.getWorks().size()<editor.getWorks().size() && !e.getUsername().equals(magazine.getMainEditor().getUsername())){
                editor=e;
            }
        }


        if(editors.size()>0){
            work.setEditor(editor);
        }else{
            work.setEditor(magazine.getMainEditor());
        }
        workService.saveWork(work);
        delegateExecution.setVariable("editorSA",work.getEditor().getUsername());
    }
}
