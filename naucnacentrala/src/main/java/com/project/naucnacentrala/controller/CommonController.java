package com.project.naucnacentrala.controller;

import com.project.naucnacentrala.dto.NewMagazineDTO;
import com.project.naucnacentrala.model.Editor;
import com.project.naucnacentrala.model.Magazine;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/common")
public class CommonController {

    @Autowired
    private ScienceAreaService scienceAreaService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private ReviewerService reviewerService;

    @Autowired
    private EditorService editorService;

    @Autowired
    private MagazineService magazineService;

    @GetMapping(path = "/getScienceAreas",
    produces = "application/json")
    public @ResponseBody ResponseEntity getScienceAreeas(){
        List<ScienceArea> scienceAreas=this.scienceAreaService.findAll();
        return new ResponseEntity(scienceAreas, HttpStatus.OK);
    }

    @PostMapping(path="/getSAReviewers",
    consumes = "application/json",
    produces = "application/json")
    public @ResponseBody ResponseEntity getReviewers(@RequestBody String[] choosenAreas){
        Set<SystemUser> reviewers=this.reviewerService.findReviewersOfAreas(choosenAreas);

        return new ResponseEntity(reviewers,HttpStatus.OK);
    }

    @GetMapping(path="/getFreeEditors",
    produces="application/json")
    public @ResponseBody ResponseEntity getFreeEditors(){
        Set<Editor> editors=this.editorService.findFreeEditors();

        return new ResponseEntity(editors,HttpStatus.OK);
    }

    @GetMapping(path="/getMyMagazine",
            produces = "application/json")
    public @ResponseBody ResponseEntity getMyMagazine(){

        Editor editor=this.editorService.findEditorByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if(editor==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Magazine magazine=this.magazineService.getMagazineByMainEditor(editor);

        if(magazine==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(magazine,HttpStatus.OK);
    }

    @PostMapping(path="/sendCorrection",
    consumes = "application/json")
    public @ResponseBody ResponseEntity fixMagazine(@RequestBody NewMagazineDTO magazineDTO){

        Magazine magazine=this.magazineService.findOneById(magazineDTO.getId());
        magazine.setStatus("request");
        magazine.setIssn(magazineDTO.getIssn());
        magazine.setName(magazineDTO.getName());
        if(magazineDTO.getMembershipType().equals("1")){
            magazine.setOpenAccess(true);
        }else if(magazineDTO.getMembershipType().equals("2")){
            magazine.setOpenAccess(false);
        }
        this.magazineService.saveMagazine(magazine);

        return new ResponseEntity(HttpStatus.OK);
    }
}
