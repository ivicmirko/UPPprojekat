package com.project.naucnacentrala.serviceIMP;

import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.repository.ScienceAreaRepository;
import com.project.naucnacentrala.repository.SystemUserRepository;
import com.project.naucnacentrala.service.ReviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReviewerServiceImp implements ReviewerService {

    @Autowired
    private ScienceAreaRepository scienceAreaRepository;

    @Autowired
    private SystemUserRepository systemUserRepository;


    @Override
    public Set<SystemUser> findReviewersOfAreas(String[] areas) {
        Set<ScienceArea> areass=new HashSet<>();
        for(int i=0;i<areas.length;i++){
            ScienceArea scienceArea=this.scienceAreaRepository.findOneById(Long.parseLong(areas[i]));
            areass.add(scienceArea);
        }
        return systemUserRepository.findSystemUsersByReviewerAndScienceAreasIn("yes",areass);
    }
}
