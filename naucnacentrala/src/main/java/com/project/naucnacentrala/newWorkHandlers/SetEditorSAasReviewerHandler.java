package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.ReviewerWork;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.EditorService;
import com.project.naucnacentrala.service.ReviewerWorkService;
import com.project.naucnacentrala.service.SystemUserService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetEditorSAasReviewerHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    EditorService editorService;

    @Autowired
    ReviewerWorkService reviewerWorkService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U UREDNIK SA KAO RECENZNET");
        Long workId=(Long) delegateExecution.getVariable("wokrId");
        Work work=new Work();
        try{
            work=workService.findById(workId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        SystemUser systemUser=this.systemUserService.findByUsername(work.getEditor().getUsername());

        ReviewerWork reviewerWork=new ReviewerWork();
        reviewerWork.setWork(work);
        reviewerWork.setSystemUser(systemUser);
        this.reviewerWorkService.saveReviewWork(reviewerWork);

        List<String> reviewersList=new ArrayList<>();
        reviewersList.add(systemUser.getUsername());

        delegateExecution.setVariable("reviewersList",reviewersList);

    }
}
