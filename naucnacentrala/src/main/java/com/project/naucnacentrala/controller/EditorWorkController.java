package com.project.naucnacentrala.controller;

import com.project.naucnacentrala.common.Utils;
import com.project.naucnacentrala.dto.ArrivedWorkDTO;
import com.project.naucnacentrala.dto.ReviewedWorkDTO;
import com.project.naucnacentrala.dto.ReviewesWorkDTO;
import com.project.naucnacentrala.jwt.JwtTokenProvider;
import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.service.*;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.calendar.DateTimeUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/editorWork")
public class EditorWorkController {

    @Autowired
    IdentityService identityService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    TaskService taskService;

    @Autowired
    FormService formService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ScienceAreaService scienceAreaService;

    @Autowired
    private MagazineService magazineService;

    @Autowired
    private WorkService workService;

    @Autowired
    private EditorService editorService;

    @Autowired
    ReviewerWorkService reviewerWorkService;

    @GetMapping(path = "/getArrivedWorks",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    ResponseEntity getArrivedWorks() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Editor editor = new Editor();
        try {
            editor = editorService.findEditorByUsername(username);
            System.out.println("Editor=" + editor.getUsername());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde urednika!");
        }
        Magazine magazine = new Magazine();
        try {
            magazine = this.magazineService.getMagazineByMainEditor(editor);
            System.out.println("Casopis=" + magazine.getName() + " Broj radova=" + magazine.getWorks().size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da pronadje casopis");
        }
        List<Work> arrivedWorks = new ArrayList<>();

        try {
            arrivedWorks = this.workService.findByMagazineAndStatus(magazine, WorkStatus.requested);
            System.out.println("Broj radovaa=" + arrivedWorks.size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde radove!");
        }
        List<ArrivedWorkDTO> retVal = new ArrayList<>();
        for (Work work : arrivedWorks) {
            ArrivedWorkDTO arrivedWorkDTO = new ArrivedWorkDTO();
            arrivedWorkDTO.setAuthorName(work.getAuthor().getName());
            arrivedWorkDTO.setAuthorSurname(work.getAuthor().getSurname());
            arrivedWorkDTO.setId(work.getId());
            arrivedWorkDTO.setTitle(work.getTitle());
            arrivedWorkDTO.setProcessId(work.getProcessId());
            retVal.add(arrivedWorkDTO);

        }
        return new ResponseEntity(retVal, HttpStatus.OK);
    }

    @GetMapping(path = "/getCheckWorkForm/{processId}",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getCheckWorkForm(@PathVariable String processId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Author author = this.authorService.findOneByUsername(username);
        } catch (Exception e) {
            System.out.println("NIKO NIJE ULOGOVAN");
        }

        Task task = taskService.createTaskQuery().active().taskDefinitionKey("MainEditorCheckWorkDetails").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormField> properties = taskFormData.getFormFields();

        return new FormFieldsDto(task.getId(), processId, properties);
    }


    @GetMapping(path = "/detailsCorrect/{processId}/{workId}",
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
        work.setProcessId(processId);
        work.setWorkStatus(WorkStatus.pdfFormatChecking);
        workService.saveWork(work);

        runtimeService.setVariable(processId, "workRelevant", true);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("MainEditorCheckWorkDetails").processInstanceId(processId).singleResult();
        taskService.complete(task.getId());

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

    @GetMapping(path = "/detailsDenied/{processId}/{workId}",
            produces = "application/json")
    public @ResponseBody
    ResponseEntity detailsUncorrect(@PathVariable String processId, @PathVariable Long workId) {
        Work work = new Work();
        try {
            work = this.workService.findById(workId);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje rad s ovim id");
        }
        work.setWorkStatus(WorkStatus.denied);
        work.setProcessId(processId);
        workService.saveWork(work);

        runtimeService.setVariable(processId, "workRelevant", false);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("MainEditorCheckWorkDetails").processInstanceId(processId).singleResult();
        System.out.println("Task id=" + task.getId());
        taskService.complete(task.getId());

        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping(path = "/worksForPdfCheck",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    ResponseEntity getWorksForPdfCheck() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Editor editor = new Editor();
        try {
            editor = editorService.findEditorByUsername(username);
            System.out.println("Editor=" + editor.getUsername());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje urednika!");
        }
        Magazine magazine = new Magazine();
        try {
            magazine = this.magazineService.getMagazineByMainEditor(editor);
            System.out.println("Casopis=" + magazine.getName() + " Broj radova=" + magazine.getWorks().size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da pronadje casopis");
        }
        List<Work> arrivedWorks = new ArrayList<>();

        try {
            arrivedWorks = this.workService.findByMagazineAndStatus(magazine, WorkStatus.pdfFormatChecking);
            System.out.println("Broj radovaa=" + arrivedWorks.size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde radove!");
        }
        List<ArrivedWorkDTO> retVal = new ArrayList<>();
        for (Work work : arrivedWorks) {
            ArrivedWorkDTO arrivedWorkDTO = new ArrivedWorkDTO();
            arrivedWorkDTO.setAuthorName(work.getAuthor().getName());
            arrivedWorkDTO.setAuthorSurname(work.getAuthor().getSurname());
            arrivedWorkDTO.setId(work.getId());
            arrivedWorkDTO.setTitle(work.getTitle());
            arrivedWorkDTO.setProcessId(work.getProcessId());
            retVal.add(arrivedWorkDTO);

        }
        return new ResponseEntity(retVal, HttpStatus.OK);
    }

    @GetMapping(path = "/approvePdf/{workId}")
    public @ResponseBody
    ResponseEntity approvePdf(@PathVariable Long workId) {
        Work work = new Work();
        try {
            work = workService.findById(workId);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje rad!");
        }
        work.setWorkStatus(WorkStatus.addReviewers);
        workService.saveWork(work);

        String processId = work.getProcessId();
        runtimeService.setVariable(processId, "correctPdfFormtat", true);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("CheckingPDFFormat").processInstanceId(processId).singleResult();
        taskService.complete(task.getId());

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/needToFixPdf/{workId}")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getCommentForm(@PathVariable Long workId) {

        Work work = new Work();
        try {
            work = workService.findById(workId);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje rad!");
        }
        work.setWorkStatus(WorkStatus.fix);
        workService.saveWork(work);

        String processId = work.getProcessId();
        runtimeService.setVariable(processId, "correctPdfFormtat", false);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("CheckingPDFFormat").processInstanceId(processId).singleResult();
        taskService.complete(task.getId());

        //dovde
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        Task task2 = taskService.createTaskQuery().active().taskDefinitionKey("CommentSettingEditTime").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task2.getId());
        List<FormField> properties = taskFormData.getFormFields();

        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @PostMapping(path = "/sendCommentForPdf/{processId}/{taskId}")
    public @ResponseBody ResponseEntity sendPdfToFix(@PathVariable String processId, @PathVariable String taskId,@RequestBody List<FormSubmissionDto> dto){

        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("CommentSettingEditTime").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        try {

        }catch (NullPointerException e){
            System.out.println("Kdo datuma puklo");
        }

        Object temp=null;

        for (int i = 0; i < dto.size(); i++) {

            if (dto.get(i).getFieldId().equals("editPdfDeadLine")) {
                temp= DateTimeUtil.parseDate(dto.get(i).getFieldValue());
            }
        }

        map.put("editPdfDeadLine",temp);

        formService.submitTaskForm(task.getId(), map);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path="/worksWithoutReviewers",
    produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody ResponseEntity getWorksWithoutReviewersForm(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Editor editor = new Editor();
        try {
            editor = editorService.findEditorByUsername(username);
            System.out.println("Editor=" + editor.getUsername());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje urednika!");
        }
        Magazine magazine = new Magazine();
        try {
            magazine = this.magazineService.getMagazineByEditor(editor);
            System.out.println("Casopis=" + magazine.getName() + " Broj radova=" + magazine.getWorks().size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da pronadje casopis");
        }
        List<Work> worksForReviewing = new ArrayList<>();

        try {
            worksForReviewing = this.workService.findByMagazineAndStatus(magazine, WorkStatus.addReviewers);
            System.out.println("Broj radovaa=" + worksForReviewing.size());
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde radove!");
        }
        List<ArrivedWorkDTO> retVal = new ArrayList<>();
        for (Work work : worksForReviewing) {
            ArrivedWorkDTO arrivedWorkDTO = new ArrivedWorkDTO();
            arrivedWorkDTO.setAuthorName(work.getAuthor().getName());
            arrivedWorkDTO.setAuthorSurname(work.getAuthor().getSurname());
            arrivedWorkDTO.setId(work.getId());
            arrivedWorkDTO.setTitle(work.getTitle());
            arrivedWorkDTO.setProcessId(work.getProcessId());
            retVal.add(arrivedWorkDTO);

        }
        return new ResponseEntity(retVal, HttpStatus.OK);
    }

    @GetMapping(path = "/getWorksReviewersForm/{processId}",
            produces = "application/json")
    @PreAuthorize("hasAuthority('editor')")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getDataInputForm(@PathVariable String processId) {


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long workId=(Long) runtimeService.getVariable(processId,"workId");
        Work work=this.workService.findById(workId);
        Magazine magazine=work.getMagazine();

        Task task = taskService.createTaskQuery().active().taskDefinitionKey("ChooseReviewers").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormField> properties = taskFormData.getFormFields();

        Set<SystemUser> systemUsers = this.systemUserService.findReviewersByScienceArea(work.getScienceArea());
        for (FormField element : properties) {
            if (element.getId().equals("chooseReviewers")) {
                for (SystemUser su : systemUsers) {
                    String retString=su.getName()+" "+su.getSurname();
                    if(magazine.getReviewes().contains(su)){
                        retString+=" (u nasem je casopisu)";
                    }
                    element.getProperties().put(String.valueOf(su.getId()), retString);
                }
            }
        }
        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @PostMapping(path = "/addWorksReviewersForm/{processId}/{taskId}")
    @SuppressWarnings("Duplicates")
    public @ResponseBody FormFieldsDto addWorksReviewersForm(@PathVariable String processId, @PathVariable String taskId,@RequestBody List<FormSubmissionDto> dto){

        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("ChooseReviewers").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        runtimeService.setVariable(processId, "newReviewers", dto);

        formService.submitTaskForm(task.getId(), map);

        Task task2 = taskService.createTaskQuery().active().taskDefinitionKey("SetReviewTime").processInstanceId(processId).singleResult();
        TaskFormData taskFormData2 = formService.getTaskFormData(task2.getId());
        List<FormField> properties = taskFormData2.getFormFields();

        return new FormFieldsDto(task2.getId(), processId, properties);
    }

    @PostMapping(path = "/setReviewingTimeForm/{processId}/{taskId}")
    @SuppressWarnings("Duplicates")
    public @ResponseBody ResponseEntity setReviewingTimeForm(@PathVariable String processId, @PathVariable String taskId,@RequestBody List<FormSubmissionDto> dto){

        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("SetReviewTime").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        String temp="P"; //P10D
        for(FormSubmissionDto formField:dto) {
            if (formField.getFieldId().equals("reviewTime")) {
                temp += formField.getFieldValue()+"D";
            }
        }

        runtimeService.setVariable(processId, "reviewTimeFormated", temp);

        formService.submitTaskForm(task.getId(), map);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path="/getReviewedWorks",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody ResponseEntity getReviewedWorks(){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        Editor editor=editorService.findEditorByUsername(username);
        System.out.println(editor.getUsername());
        List<Work> works = workService.findByEditorAndStatus(editor, WorkStatus.makingDecision);
        System.out.println(works.size());
        List<ArrivedWorkDTO> retVal = new ArrayList<>();
        for (Work work : works) {
            ArrivedWorkDTO arrivedWorkDTO = new ArrivedWorkDTO();
            arrivedWorkDTO.setAuthorName(work.getAuthor().getName());
            arrivedWorkDTO.setAuthorSurname(work.getAuthor().getSurname());
            arrivedWorkDTO.setId(work.getId());
            arrivedWorkDTO.setTitle(work.getTitle());
            arrivedWorkDTO.setProcessId(work.getProcessId());
            retVal.add(arrivedWorkDTO);

        }
        return new ResponseEntity(retVal, HttpStatus.OK);
    }

    @GetMapping(path="openReviewedWork/{processId}/{workId}",
    produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody ResponseEntity openReviewedWork(@PathVariable String processId,@PathVariable Long workId){

        Work work=this.workService.findById(workId);
        Set<ReviewerWork> reviewerWorks=new HashSet<>();
        reviewerWorks=this.reviewerWorkService.findAllByWork(work);
        List<ReviewedWorkDTO> retVal=new ArrayList<>();
        for(ReviewerWork reviewerWork:reviewerWorks){
            ReviewedWorkDTO reviewedWorkDTO=new ReviewedWorkDTO();
            reviewedWorkDTO.setAuthorComment(reviewerWork.getAuthorComment());
            reviewedWorkDTO.setEditorComment(reviewerWork.getEditorComment());
            reviewedWorkDTO.setDate(reviewerWork.getDate());
            reviewedWorkDTO.setRecommendation(reviewerWork.getReccomendation());
            reviewedWorkDTO.setId(reviewerWork.getWork().getId());
            retVal.add(reviewedWorkDTO);
        }
        ReviewesWorkDTO reviewesWorkDTO=new ReviewesWorkDTO();
        reviewesWorkDTO.setAnswer(work.getAnswer());
        reviewesWorkDTO.setReviewedWorkDTOS(retVal);

        return new ResponseEntity(reviewesWorkDTO,HttpStatus.OK);
    }

    @GetMapping(path="makeDecision/{processId}/{decision}",
    produces = "application/json")
    public @ResponseBody ResponseEntity postDecision(@PathVariable String processId, @PathVariable int decision){
        Long workId=(Long) runtimeService.getVariable(processId,"workId");
        Work work=workService.findById(workId);

        work.setWorkStatus(WorkStatus.needChages);
        this.workService.saveWork(work);
        runtimeService.setVariable(processId,"decision",decision);
        Task task = taskService.createTaskQuery().active().taskDefinitionKey("EditorMakesDecision").processInstanceId(processId).singleResult();
        taskService.complete(task.getId());

        return new ResponseEntity(HttpStatus.OK);
    }

//
//    @GetMapping(path = "/do/{processId}/{workId}",
//            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public @ResponseBody
//    ResponseEntity<InputStreamResource> detailsCorrect(@PathVariable String processId, @PathVariable Long workId, HttpServletResponse response, HttpServletRequest request) throws IOException {
//        Work work = new Work();
//        try {
//            work = this.workService.findById(workId);
//        } catch (NullPointerException e) {
//            System.out.println("Ne moze da nadje rad s ovim id");
//        }
//        work.setProcessId(processId);
//        work.setWorkStatus(WorkStatus.pdfFormatChecking);
//        workService.saveWork(work);
//
//        runtimeService.setVariable(processId, "workRelevant", true);
//        Task task = taskService.createTaskQuery().active().taskDefinitionKey("MainEditorCheckWorkDetails").processInstanceId(processId).singleResult();
//        taskService.complete(task.getId());
//
//        String[] path = work.getProposalWorkPath().split("/");
//        String fileName = path[path.length - 1];
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
//        File file = new File(work.getProposalWorkPath());
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(resource);
//    }
}
