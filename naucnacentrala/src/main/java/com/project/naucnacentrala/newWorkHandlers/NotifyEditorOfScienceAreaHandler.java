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
public class NotifyEditorOfScienceAreaHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U OBAVESTAVANJE UREDNIKA NAUCNE OBLASTI");

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=new Work();
        try {
            work=workService.findById(workId);
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje rad");
            e.printStackTrace();
        }

        Editor editor=work.getEditor();

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(editor.getEmail());
            message.setSubject("Notifikacija o novom radu");


            message.setText("Postovani/a "+editor.getSurname() + " " + editor.getName() + "\n\n"+
                            "Izabrani ste kao urednik naucne oblasti u radu "+work.getTitle()+", autora\n" +
                    work.getAuthor().getSurname()+" "+work.getAuthor().getName()+"."+
                    "\n\n Vasa NaucnaCentrala");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
