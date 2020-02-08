package com.project.naucnacentrala.newWorkHandlers;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class StartProcessHandler implements ExecutionListener {

    @Autowired
    IdentityService identityService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {

        String username= SecurityContextHolder.getContext().getAuthentication().getName();

        if(username!=null && !username.equals("") ){
            delegateExecution.setVariable("activator",username);
        }

    }
}
