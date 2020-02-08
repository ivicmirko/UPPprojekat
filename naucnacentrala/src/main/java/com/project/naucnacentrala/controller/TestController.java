package com.project.naucnacentrala.controller;

import org.camunda.bpm.engine.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    @GetMapping("/test1")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> test(){

        System.out.println("usaooo");
        return new ResponseEntity(HttpStatus.OK);
    }



}
