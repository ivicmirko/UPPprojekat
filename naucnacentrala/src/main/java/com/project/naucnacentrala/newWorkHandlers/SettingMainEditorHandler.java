package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.MagazineService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingMainEditorHandler implements JavaDelegate {

    @Autowired
    private WorkService workService;

    @Autowired
    private MagazineService magazineService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U SET_MAIN_EDITOR");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=new Work();
        try {
            work=this.workService.findById(workId);
        }catch (NullPointerException e){
            System.out.println("ne moze da nadje rad");
        }
        Editor editor=new Editor();

        try {
            editor=work.getMagazine().getMainEditor();
        }catch (NullPointerException e){
            System.out.println("ne moze da nadje urednika");
        }

        delegateExecution.setVariable("mainEditor", editor.getUsername());
    }
}
