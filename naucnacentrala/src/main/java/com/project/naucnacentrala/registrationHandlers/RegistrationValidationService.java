package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.FormSubmissionDto;

import com.project.naucnacentrala.service.ScienceAreaService;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationValidationService implements JavaDelegate {

    @Autowired
    IdentityService identityService;

    @Autowired
    ScienceAreaService scienceAreaService;

    @Autowired
    SystemUserService systemUserService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("DATA VALIDATION TASK");

        List<FormSubmissionDto> registration = (List<FormSubmissionDto>) delegateExecution.getVariable("registration");

        String email="";
        String username="";
        for(int i=0; i<registration.size(); i++) {
            if (registration.get(i).getFieldId().equals("formEmail")) {
                email = registration.get(i).getFieldValue();
            } else if (registration.get(i).getFieldId().equals("formUsername")) {
                username = registration.get(i).getFieldValue();
            }
        }

        int check=0;
        check=systemUserService.checkCredentials(username, email);

        if(check !=0){
            delegateExecution.setVariable("valid",false);
        }

    }
}
