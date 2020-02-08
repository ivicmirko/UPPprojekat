package com.project.naucnacentrala.newWorkHandlers;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class IsMembershipPaidHandler implements JavaDelegate {


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println("PROVERA JE LI PLACENA CLANARINA TASK");
        //treba proveriti da li autor ima validnu clanarinu, ako
        //IMA sve okej
        //NEMA prvo mora platiti
        delegateExecution.setVariable("membershipPaid", true);

    }
}
