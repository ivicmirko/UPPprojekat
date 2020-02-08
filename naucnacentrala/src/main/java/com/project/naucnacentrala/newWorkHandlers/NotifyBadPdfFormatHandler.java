package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Work;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotifyBadPdfFormatHandler implements JavaDelegate {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    WorkService workService;

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U OBAVESTAVANJE O LOSEM PDF FORMATU");

        Long workId=(Long) delegateExecution.getVariable("workId");
        Work work=new Work();
        Author author=new Author();
        try{
            work=this.workService.findById(workId);
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje rad");
        }

        try {
            author=work.getAuthor();
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje autora");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(author.getEmail());
            message.setSubject("Obavestenje o neogovarajucem formatu rada!");


            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\n\n" +
                    "Urednik casopisa smatra da PDF format Vaseg rada "+work.getTitle()+" nije odgovarajuci.\n"+
                            "U odeljku Moji radovi->Za ispravku mozete dodati Vas izmenjeni rad.\n"+
                    "Takodje tu mozete videti i zamerke koje je urednik ostavio.\n "
                    + "\n\n Vasa NaucnaCentrala");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
