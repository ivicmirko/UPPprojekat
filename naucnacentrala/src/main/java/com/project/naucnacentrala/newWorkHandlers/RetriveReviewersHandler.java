package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.SystemUserService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RetriveReviewersHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Autowired
    SystemUserService systemUserService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U RETRIVE REVIEWERS");

        Long workId=(Long) delegateExecution.getVariable("workId");

        Work work=workService.findById((workId));
        ScienceArea scienceArea=work.getScienceArea();

        Set<SystemUser> reviewers=new HashSet<>();
        reviewers=this.systemUserService.findReviewersByScienceArea(scienceArea);

        //proveriti da li strogo manje od 2, a sta raditi ako ima 1
        if(reviewers.size()<2){
            delegateExecution.setVariable("reviewersExist",false);
        }else{
            delegateExecution.setVariable("reviewersExist",true);
        }

        workService.saveWork(work);
    }
}
