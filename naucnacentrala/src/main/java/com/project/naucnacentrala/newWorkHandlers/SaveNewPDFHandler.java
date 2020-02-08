package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.model.WorkStatus;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveNewPDFHandler implements JavaDelegate {
    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=new Work();
        try {
            work=this.workService.findById(workId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        String proposalPdfPath=(String) delegateExecution.getVariable("proposalPdfFilePath");
        work.setProposalWorkPath(proposalPdfPath);
        work.setWorkStatus(WorkStatus.requested);
        workService.saveWork(work);
    }
}
