package com.project.naucnacentrala.service;

import com.project.naucnacentrala.model.SystemUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SystemUserService {

    SystemUser findByEmail(String email);
    SystemUser findByUsername(String username);
//    Authority findByName(String name);
    SystemUser saveSystemUser(SystemUser systemUser);
    List<SystemUser> findAll();
    int checkCredentials(String username,String email);
    void deleteSystemUser(SystemUser systemUser);
}
