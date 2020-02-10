package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.Author;
import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.AuthorService;
import com.project.naucnacentrala.service.EditorService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("Duplicates")
public class NotifyAuthorMainEditorHandler implements JavaDelegate {

    @Autowired
    private EditorService editorService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private WorkService workService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U TASK OBAVESTAVANJA GLAVNOG UREEDNIKA I AUTORA");
        String mainEditorUsername=(String) delegateExecution.getVariable("mainEditor");
        Long workId=(Long) delegateExecution.getVariable("workId");
        Author author=new Author();
        Editor mainEditor=new Editor();
        try {
            author=this.workService.findById(workId).getAuthor();
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje autora");
        }

        try {
            mainEditor=this.editorService.findEditorByUsername(mainEditorUsername);
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje email od glavnog urednika!");
        }

//        try {
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(author.getEmail());
//            message.setSubject("Notifikacija o poslatom radu");
//
//
//            message.setText("Postovani/a "+author.getSurname() + " " + author.getName() + "\nVas rad je prispeo u Naucnu centralu.\n" +
//                    "Vas rad je uzet u razmatranje. Bicete blagovremeno obavesteni o daljim koracima.\n "
//                    + "\n\n Vasa NaucnaCentrala");
//            javaMailSender.send(message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(mainEditor.getEmail());
//            message.setSubject("Notifikacija o novom radu");
//
//
//            message.setText("Postovani/a "+mainEditor.getSurname() + " " + mainEditor.getName() + "\nAutor "+author.getName()+" "+author.getSurname()+" je dodao nov naucni rad u Vas casopis.\n" +
//                    "\n\n Vasa NaucnaCentrala");
//            javaMailSender.send(message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
