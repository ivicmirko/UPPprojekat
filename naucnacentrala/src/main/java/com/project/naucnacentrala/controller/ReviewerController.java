package com.project.naucnacentrala.controller;

import com.project.naucnacentrala.common.Utils;
import com.project.naucnacentrala.dto.WorkForReviewingDTO;
import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.service.ReviewerWorkService;
import com.project.naucnacentrala.service.SystemUserService;
import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path="/reviewer")
public class ReviewerController {
    @Autowired
    SystemUserService systemUserService;

    @Autowired
    ReviewerWorkService reviewerWorkService;

    @Autowired
    WorkService workService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    FormService formService;

    @GetMapping(path = "getWorksForReviewing",
            produces = "application/json")
    public @ResponseBody
    ResponseEntity getWorksForReviewing() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SystemUser systemUser = this.systemUserService.findByUsername(username);

        Set<Work> works = this.reviewerWorkService.getWorksForReviewing(systemUser);
        List<WorkForReviewingDTO> retVal=new ArrayList<>();
        for(Work work:works){
            WorkForReviewingDTO temp=new WorkForReviewingDTO();
            temp.setId(work.getId());
            temp.setTitle(work.getTitle());
            temp.setKeyTerms(work.getKeyTerms());
            temp.setWorkAbstract(work.getWorkAbstract());
            temp.setProcessId(work.getProcessId());
            retVal.add(temp);
        }

        return new ResponseEntity(retVal, HttpStatus.OK);
    }

    @GetMapping(value = "/getReviewingForm/{processId}",
            produces = "application/json")
    public @ResponseBody
    FormFieldsDto getReviewongForm(@PathVariable String processId) {

        Long workId=(Long) runtimeService.getVariable(processId,"workId");
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        Task task = taskService.createTaskQuery().active().taskDefinitionKey("ReviewingWork").taskAssignee(username).processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormField> properties = taskFormData.getFormFields();

        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @GetMapping(path = "/downloadPdf/{processId}/{workId}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    ResponseEntity<InputStreamResource> detailsCorrect(@PathVariable String processId, @PathVariable Long workId, HttpServletResponse response, HttpServletRequest request) throws IOException {


        Work work = new Work();
        try {
            work = this.workService.findById(workId);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje rad s ovim id");
        }

        String[] path = work.getProposalWorkPath().split("/");
        String fileName = path[path.length - 1];

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        File file = new File(work.getProposalWorkPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }


        @PostMapping(value = "/postReviewingForm/{processId}/{taskId}",
            produces = "application/json")
    public @ResponseBody
    ResponseEntity postReviewingForm(@PathVariable String processId, @PathVariable String taskId, @RequestBody List<FormSubmissionDto> dto) {

            String username=SecurityContextHolder.getContext().getAuthentication().getName();
            HashMap<String, Object> map = Utils.mapListToDto(dto);
            Task task = taskService.createTaskQuery().active().taskDefinitionKey("ReviewingWork").taskAssignee(username).processInstanceId(processId).singleResult();
            TaskFormData taskFormData = formService.getTaskFormData(task.getId());

            Long workId=(Long) runtimeService.getVariable(processId,"workId");

            Work work=this.workService.findById(workId);
            SystemUser systemUser=this.systemUserService.findByUsername(username);

            ReviewerWork reviewerWork=new ReviewerWork();
            ReviewerWorkKey reviewerWorkKey=new ReviewerWorkKey();
            reviewerWorkKey.setReviewerId(systemUser.getId());
            reviewerWorkKey.setWorkId(work.getId());
            try {
                reviewerWork=this.reviewerWorkService.findByKey(reviewerWorkKey);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            for(int i=0; i<dto.size(); i++) {
                if (dto.get(i).getFieldId().equals("commentForAuthor")) {
                    reviewerWork.setAuthorComment(dto.get(i).getFieldValue());
                }else if (dto.get(i).getFieldId().equals("commentForEditor")) {
                    reviewerWork.setEditorComment(dto.get(i).getFieldValue());
                }else if (dto.get(i).getFieldId().equals("commentForAuthor")) {
                    reviewerWork.setAuthorComment(dto.get(i).getFieldValue());
                }else if (dto.get(i).getFieldId().equals("recommendation")) {
                    reviewerWork.setReccomendation(dto.get(i).getFieldValue());
                }
            }
            reviewerWork.setReviewed(true);
            reviewerWork.setDate(new Date());
            formService.submitTaskForm(task.getId(), map);




            return new ResponseEntity(HttpStatus.OK);

    }
}
