package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.service.ReviewerWorkService;
import com.project.naucnacentrala.service.SystemUserService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveReviewersHandler implements JavaDelegate {

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    ReviewerWorkService reviewerWorkService;

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U CUVANJE RECENZENATA");

        Long workId = (Long) delegateExecution.getVariable("workId");
        Work work = this.workService.findById(workId);
        List<String> reviewersList = new ArrayList<>();
        List<FormSubmissionDto> reviewersDTO = (List<FormSubmissionDto>) delegateExecution.getVariable("newReviewers");

        work.setWorkStatus(WorkStatus.reviewing);
        for (int i = 0; i < reviewersDTO.size(); i++) {
            if (reviewersDTO.get(i).getFieldId().equals("choseReviewers")) {
//                ScienceArea scienceArea=this.scienceAreaService.findOneById(Long.parseLong(registration.get(i).getFieldValue()));
//                if(scienceArea != null){
//                    user.getScienceAreas().add(scienceArea);
//                }
                String allReviewers = reviewersDTO.get(i).getFieldValue();
                String[] reviewersId = allReviewers.split(",");
                SystemUser systemUser;
                for (int ii = 0; ii < reviewersId.length; ii++) {
                    systemUser = this.systemUserService.findOneById(Long.parseLong(reviewersId[ii]));
                    if (systemUser != null) {
                        ReviewerWork reviewerWork = new ReviewerWork();
                        reviewerWork.setSystemUser(systemUser);
                        reviewerWork.setWork(work);
                        ReviewerWorkKey reviewerWorkKey=new ReviewerWorkKey();
                        reviewerWorkKey.setReviewerId(systemUser.getId());
                        reviewerWorkKey.setWorkId(work.getId());
                        reviewerWork.setReviewerWorkKey(reviewerWorkKey);
                        this.reviewerWorkService.saveReviewWork(reviewerWork);
                        reviewersList.add(systemUser.getUsername());
                    }
                }
            }

        }
        delegateExecution.setVariable("reviewersList",reviewersList);
    }
}
