package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.model.WorkStatus;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotifyAboutChangesHandler implements JavaDelegate {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U PROMENE");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=this.workService.findById(workId);

        Author author=work.getAuthor();
        int decision=(int)delegateExecution.getVariable("decision");
        String changes="";
        if(decision==2){
            changes="male";
        }else if(decision==3){
            changes="velike";
        }
//        try {
////
////            SimpleMailMessage message = new SimpleMailMessage();
////            message.setTo(author.getEmail());
////            message.setSubject("Notifikacija o radu");
////
////
////            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\nVasem radu s naslovom "+work.getTitle()+" su potrebne "+changes+" izmene!"+
////                    "\n\n Vasa NaucnaCentrala");
////            javaMailSender.send(message);
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
    }
}
