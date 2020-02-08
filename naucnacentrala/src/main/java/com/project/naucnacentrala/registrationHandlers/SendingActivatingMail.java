package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.FormSubmissionDto;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendingActivatingMail implements JavaDelegate {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    SystemUserService systemUserService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("USAO U SLANJE MAIL");
        String receiverMail="";

        List<FormSubmissionDto> registration = (List<FormSubmissionDto>) delegateExecution.getVariable("registration");
        for(FormSubmissionDto dto: registration){
            if(dto.getFieldId().equals("formEmail")){
                receiverMail = dto.getFieldValue();
            }
        }
        System.out.println("Maiil"+receiverMail);
        SystemUser user = systemUserService.findByEmail(receiverMail);
        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiverMail);
            message.setSubject("Potvrda registracije");

            String confirmationUrl
                    =  "http://localhost:4200/activation/" + user.getUsername() + "/" + delegateExecution.getProcessInstanceId();
            message.setText("Postovani/a "+user.getSurname() + " " + user.getName() + "\ndobrodosli u Naucnu centralu.\n" +
                    "Kako bi zavrsili Vas proces registracije, kliknite na sledeci link\n "
                    + confirmationUrl + "\n\n Vasa NaucnaCentrala");
            emailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
