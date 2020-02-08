package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.service.*;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class SavingUnpublishedWorkHandler implements JavaDelegate {

    @Autowired
    IdentityService identityService;

    @Autowired
    ScienceAreaService scienceAreaService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    MagazineService magazineService;

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println("USAO U CUVANJE NOVOG RADA");
        List<FormSubmissionDto> workDetails = (List<FormSubmissionDto>) delegateExecution.getVariable("workDetails");
        Long magazineId=(Long) delegateExecution.getVariable("magazineId");
        Magazine magazine=new Magazine();
        String proposalPdfPath=(String) delegateExecution.getVariable("proposalPdfFilePath");
        System.out.println("PDF="+proposalPdfPath);
        String username=(String) delegateExecution.getVariable("activator");
        Author author=new Author();
        try{
            author=this.authorService.findOneByUsername(username);
        }catch (NullPointerException e){
            System.out.println("Ne moze da pronadje autora");
        }

        try{
            magazine=this.magazineService.findOneById(magazineId);
        }catch (NullPointerException e){
            System.out.println("Ne moze da nadje casopis");
        }

        Work work=new Work();
        String receiverMail = "";
        work.setWorkStatus(WorkStatus.requested);
        work.setProposalWorkPath(proposalPdfPath);
        work.setAuthor(author);
        work.setMagazine(magazine);

        for(int i=0; i<workDetails.size(); i++){
            if(workDetails.get(i).getFieldId().equals("titleWorkForm")){
                work.setTitle(workDetails.get(i).getFieldValue());
            }else if(workDetails.get(i).getFieldId().equals("abstractWorkForm")){
                work.setWorkAbstract(workDetails.get(i).getFieldValue());
            }else if(workDetails.get(i).getFieldId().equals("keyTermsWorkForm")){
                String allKeyTerms=workDetails.get(i).getFieldValue();
//                String [] terms=allKeyTerms.split(",");
//                List<String> termsList=new ArrayList<>();
//                for(int j=0;j < terms.length;j++){
//                    termsList.add(terms[j]);
//                }
                work.setKeyTerms(allKeyTerms);
            }else if(workDetails.get(i).getFieldId().equals("scienceAreaForma")){
                ScienceArea scienceArea=new ScienceArea();
                try {
                    scienceArea=this.scienceAreaService.findOneById(Long.parseLong(workDetails.get(i).getFieldValue()));
                }catch (NullPointerException e){
                    System.out.println("Ne moze da pronadje naucnu oblast");
                }
                work.setScienceArea(scienceArea);
            }
        }
        work.setProcessId(delegateExecution.getProcessInstanceId());
        work=this.workService.saveWork(work);
        delegateExecution.setVariable("workId", work.getId());

    }
}
