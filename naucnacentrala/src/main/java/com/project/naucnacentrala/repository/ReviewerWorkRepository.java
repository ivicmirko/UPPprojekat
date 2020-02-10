package com.project.naucnacentrala.repository;

import com.project.naucnacentrala.model.ReviewerWork;
import com.project.naucnacentrala.model.ReviewerWorkKey;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReviewerWorkRepository extends JpaRepository<ReviewerWork,Long> {

    Set<ReviewerWork> findAllBySystemUser(SystemUser systemUser);
    ReviewerWork findOneByReviewerWorkKey(ReviewerWorkKey reviewerWorkKey);
    Set<ReviewerWork> findAllByWork(Work work);
}
