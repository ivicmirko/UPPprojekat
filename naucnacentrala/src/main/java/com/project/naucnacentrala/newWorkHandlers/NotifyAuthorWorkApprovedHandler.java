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
public class NotifyAuthorWorkApprovedHandler implements JavaDelegate {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("OBAVESTI AUTORA DA JE RAD PRIHVACEN");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=this.workService.findById(workId);

        work.setWorkStatus(WorkStatus.approved);
        Author author=work.getAuthor();

//        try {
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(author.getEmail());
//            message.setSubject("Notifikacija o radu");
//
//
//            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\nVas rad s naslovom "+work.getTitle()+" je prihvacen!"+
//                    "\n\n Vasa NaucnaCentrala");
//            javaMailSender.send(message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
