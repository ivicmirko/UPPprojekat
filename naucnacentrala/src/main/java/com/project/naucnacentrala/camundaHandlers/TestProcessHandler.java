package com.project.naucnacentrala.camundaHandlers;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


    @Service
    public class TestProcessHandler implements ExecutionListener {
        @Autowired
        IdentityService identityService;

        @Override
        public void notify(DelegateExecution execution) throws Exception {
            System.out.println("Usaoooo");
            System.out.println("Poceo proces");
            List<User> users = identityService.createUserQuery().userIdIn("pera", "mika", "zika").list();
            if(users.isEmpty() ) {

                User user1 = identityService.newUser("pera");
                user1.setEmail("pera@mail.com");
                user1.setFirstName("Pera");
                user1.setLastName("Peric");
                user1.setPassword("pass");
                identityService.saveUser(user1);

                User user2 = identityService.newUser("mika");
                user2.setEmail("mika@mail.com");
                user2.setFirstName("Mika");
                user2.setLastName("Mikic");
                user2.setPassword("pass");
                identityService.saveUser(user2);

                User user3 = identityService.newUser("zika");
                user3.setEmail("zika@mail.com");
                user3.setFirstName("Zika");
                user3.setLastName("Zikic");
                user3.setPassword("pass");

                identityService.saveUser(user3);
            }

        }
    }

