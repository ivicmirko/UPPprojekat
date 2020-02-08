package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.SystemUser;

import java.util.List;
import java.util.Set;

public interface ReviewerService {

    public Set<SystemUser> findReviewersOfAreas(String[] areas);

}
