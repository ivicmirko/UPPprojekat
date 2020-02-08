package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivatingUserService implements JavaDelegate {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private SystemUserService systemUserService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("AKTIVACIJA KORISNIKA");

        String username = (String) delegateExecution.getVariable("username");
        System.out.println("Username je"+username);
        SystemUser user  = systemUserService.findByUsername(username);
        if(user == null || user.isEnabled()) {
            System.out.println("Nema ovog korisnika, ili je vec aktiviran");
        }else{
            user.setEnabled(true);
            systemUserService.saveSystemUser(user);

        }
    }
}
