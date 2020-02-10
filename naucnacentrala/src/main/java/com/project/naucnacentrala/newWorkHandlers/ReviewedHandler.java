package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.model.WorkStatus;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

@Service
public class ReviewedHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("RAD RECENZIRAN");

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=workService.findById(workId);
        work.setWorkStatus(WorkStatus.makingDecision);
        workService.saveWork(work);
    }
}
