package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.ReviewerWork;
import com.project.naucnacentrala.model.ReviewerWorkKey;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.model.Work;

import java.util.Set;

public interface ReviewerWorkService {

    ReviewerWork saveReviewWork(ReviewerWork reviewerWork);

    Set<Work> getWorksForReviewing(SystemUser systemUser);
    ReviewerWork findByKey(ReviewerWorkKey reviewerWorkKey);
    Set<ReviewerWork> findAllByWork(Work work);
}
