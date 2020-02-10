package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.service.SystemUserService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingNewReviewerHandler implements JavaDelegate {
    @Autowired
    WorkService workService;

    @Autowired
    SystemUserService systemUserService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("CUVANJE NOVOG RECENZNETA");
        List<String> usernames=(List<String>) delegateExecution.getVariable("reviewersList");
        String newUsername=(String) delegateExecution.getVariable("newUsername");
        String oldUsername=(String) delegateExecution.getVariable("oldUsername");

        System.out.println("new"+newUsername);
        System.out.println("old"+oldUsername);
        if(!newUsername.equals(oldUsername)){

            for(int i =0;i<usernames.size();i++){
                if(usernames.get(i).equals(oldUsername)) {
                    usernames.get(i).replace(oldUsername,newUsername);
                }
            }
        }

    }
}
