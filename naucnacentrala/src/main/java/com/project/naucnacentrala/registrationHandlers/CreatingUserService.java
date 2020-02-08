package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.FormSubmissionDto;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.ScienceAreaService;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatingUserService implements JavaDelegate {
    @Autowired
    IdentityService identityService;

    @Autowired
    ScienceAreaService scienceAreaService;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    SystemUserService systemUserService;

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U TASK KREIRANJA KORISNIKA");
        List<FormSubmissionDto> registration = (List<FormSubmissionDto>) delegateExecution.getVariable("registration");

        SystemUser user=new SystemUser();
        String receiverMail = "";

        for(int i=0; i<registration.size(); i++){
            if(registration.get(i).getFieldId().equals("formName")){
                user.setName(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formSurname")){
                user.setSurname(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formTitle")){
                user.setTitle(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formCity")){
                user.setCity(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formCountry")){
                user.setCountry(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formEmail")){
                receiverMail = registration.get(i).getFieldValue();
                user.setEmail(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formUsername")){
                user.setUsername(registration.get(i).getFieldValue());
            }else if(registration.get(i).getFieldId().equals("formPassword")){
                String salt = BCrypt.gensalt();
                String hashedPass = BCrypt.hashpw(registration.get(i).getFieldValue(), salt);
                user.setPassword(hashedPass);
            }else if(registration.get(i).getFieldId().equals("formReviewer")){

                if(registration.get(i).getFieldValue().equals(false) || registration.get(i).getFieldValue().equals("false") || registration.get(i).getFieldValue().equals("")){
                    user.setReviewer("no");
                }else if(registration.get(i).getFieldValue().equals(true) || registration.get(i).getFieldValue().equals("true")){
                    user.setReviewer("asking");
                }
            }else if(registration.get(i).getFieldId().equals("formScienceArea1")){
                System.out.println("Dodavanje naucnih oblasti");
//                ScienceArea scienceArea=this.scienceAreaService.findOneById(Long.parseLong(registration.get(i).getFieldValue()));
//                if(scienceArea != null){
//                    user.getScienceAreas().add(scienceArea);
//                }
                String allAreas=registration.get(i).getFieldValue();
                String [] areas=allAreas.split(",");
                ScienceArea scienceArea;
                for(int ii=0; ii< areas.length; ii++ ) {
                    System.out.println(areas[ii]);
                    scienceArea=scienceAreaService.findOneById(Long.parseLong(areas[ii]));
                    System.out.println("oblast"+scienceArea.getName());
                    if(scienceArea != null) {
                        user.getScienceAreas().add(scienceArea);
                    }
                }
            }
        }
        delegateExecution.setVariable("registration", registration);

        user.setEnabled(false);
        user = systemUserService.saveSystemUser(user);

    }
}
