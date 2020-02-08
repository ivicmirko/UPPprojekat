package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Author;
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
public class NotifyThemaNotGoodHandler implements JavaDelegate {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    WorkService workService;



    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U OBAVESTAVANJE O ODBIJENOM RADU1");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Author author=new Author();
        Work work=new Work();
        System.out.println("Id rada="+workId);
        try{
            work=workService.findById(workId);
            System.out.println("Rad="+work.getTitle());

        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            author=work.getAuthor();
            System.out.println("Autor= "+author.getUsername());
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje autora");
            e.printStackTrace();
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(author.getEmail());
            message.setSubject("Obavestenje o odbijanju rada!");


            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\nVas rad, "+work.getTitle()+" ,je odbijen.\n" +
                    "Glavni urednik smatra, da tema Vaseg rada nije odgovarajuca za ovaj casopis.\n "
                    + "\n\n Vasa NaucnaCentrala");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.workService.deleteWork(work);
        }catch (Exception e){
            System.out.println("Greska kod brisanja");
            e.printStackTrace();
        }
    }
}
