package com.project.naucnacentrala.serviceIMP;

import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.repository.ReviewerWorkRepository;
import com.project.naucnacentrala.repository.WorkRepository;
import com.project.naucnacentrala.service.ReviewerWorkService;
import com.project.naucnacentrala.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReviewerWorkServiceImp implements ReviewerWorkService {

    @Autowired
    ReviewerWorkRepository reviewerWorkRepository;

    @Autowired
    WorkRepository workRepository;

    @Override
    public ReviewerWork saveReviewWork(ReviewerWork reviewerWork) {
        return this.reviewerWorkRepository.save(reviewerWork);
    }

    @Override
    public Set<Work> getWorksForReviewing(SystemUser systemUser) {
        Set<ReviewerWork> revWorks=this.reviewerWorkRepository.findAllBySystemUser(systemUser);
        Set<Work> retVal=new HashSet<>();
        for(ReviewerWork rw:revWorks){
            if(rw.getWork().getWorkStatus().equals(WorkStatus.reviewing) && !rw.isReviewed()){
                retVal.add(rw.getWork());
            }
        }
        return retVal;
    }

    @Override
    public ReviewerWork findByKey(ReviewerWorkKey reviewerWorkKey) {
        return this.reviewerWorkRepository.findOneByReviewerWorkKey(reviewerWorkKey);
    }

    @Override
    public Set<ReviewerWork> findAllByWork(Work work) {
        return this.reviewerWorkRepository.findAllByWork(work);
    }
}
