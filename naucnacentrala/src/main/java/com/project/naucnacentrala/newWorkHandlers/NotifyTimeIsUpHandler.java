package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotifyTimeIsUpHandler implements JavaDelegate {
    @Autowired
    WorkService workService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("ISTEKLO VREME ZA RECENZENTA");

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=this.workService.findById(workId);

        Editor editor=work.getEditor();

        Boolean revExist=(Boolean) delegateExecution.getVariable("reviewersExist");
        String processId=(String) delegateExecution.getProcessInstanceId();
        String url="http://localhost:4200/setNewReviewer/";
        String username="";
        if(revExist){
            username=(String) delegateExecution.getVariable("oneUser");
            url=url+username+"/"+processId;
        }else{
            username="Vama";
            url=url+editor.getUsername()+"/"+processId;
        }

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(editor.getEmail());
            message.setSubject("Notifikacija o isteku vremena za recenzenta");


            message.setText("Postovani/a "+editor.getSurname() + " " + editor.getName() + "\nIsteklo je vreme recenziranja recenzetu: "+username+
                    "\nMolimo da dodate novog na sledecem linku:\n"+
                    url+
                    "\n\n Vasa NaucnaCentrala");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
