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
public class NotifyAuthorWorkRejectedHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("OBAVESTI AUTORA DA JE RAD ODBIJEN");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=this.workService.findById(workId);

        work.setWorkStatus(WorkStatus.denied);
        Author author=work.getAuthor();

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(author.getEmail());
            message.setSubject("Notifikacija o radu");


            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\nVas rad s naslovom "+work.getTitle()+" je odbijen!"+
                    "\n\n Vasa NaucnaCentrala");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
