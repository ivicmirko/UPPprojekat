package com.project.naucnacentrala.newWorkHandlers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class SaveReviewTimeHandler implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U CUVANJE VREMENA RECENZIRANJA");
    }
}
