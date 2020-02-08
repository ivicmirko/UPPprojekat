package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Authority;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.repository.AuthorityRepository;
import com.project.naucnacentrala.service.AuthorService;
import com.project.naucnacentrala.service.ScienceAreaService;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfirmingReviewerService implements JavaDelegate {
    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U POTVRDU RECENZENTA");
        String username = (String) delegateExecution.getVariable("username");
        String decision=(String) delegateExecution.getVariable("decision");

        try{
            SystemUser user=systemUserService.findByUsername(username);
            List<Authority> authorities=new ArrayList<>();
            user.setReviewer(decision);
            if(decision.equals("yes")){
                Authority authority=authorityRepository.findAuthorityByName("reviewer");
                authorities.add(authority);
                user.setAuthorities(authorities);
                user=systemUserService.saveSystemUser(user);
            }else if(decision.equals("no")){
                Authority authority=authorityRepository.findAuthorityByName("author");
                authorities.add(authority);
                user.setAuthorities(authorities);
                Author author=new Author(user);
                this.systemUserService.deleteSystemUser(user);
                this.authorService.save(author);
            }
        }catch(NullPointerException e){
            System.out.println("Nema korisnika");
        }
    }
}
