package com.project.naucnacentrala.newWorkHandlers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class SaveReviewingHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U CUVANJE RECENZIJE");

        String username=(String) delegateExecution.getVariable("oneUser");
        System.out.println("Korisnik="+username);
    }
}
