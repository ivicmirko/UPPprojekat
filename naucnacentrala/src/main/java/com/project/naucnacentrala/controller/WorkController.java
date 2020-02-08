package com.project.naucnacentrala.controller;

import ch.qos.logback.core.util.ContentTypeUtil;
import com.project.naucnacentrala.common.Utils;
import com.project.naucnacentrala.dto.ArrivedWorkDTO;
import com.project.naucnacentrala.dto.MessageResponeDTO;
import com.project.naucnacentrala.jwt.JwtTokenProvider;
import com.project.naucnacentrala.model.*;
import com.project.naucnacentrala.service.*;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.calendar.DateTimeUtil;
import org.camunda.bpm.engine.rest.dto.converter.DateConverter;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.internet.ContentType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path="/work")
public class WorkController {

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


    @GetMapping(path = "/getChoosingMagazineForm",
            produces = "application/json")
    @PreAuthorize("hasAuthority('author')")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getDataInputForm() {

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("newWork");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Author author = this.authorService.findOneByUsername(username);
        } catch (Exception e) {
            System.out.println("NIKO NIJE ULOGOVAN");
        }

        runtimeService.setVariable(pi.getId(), "activator", username);


        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        List<FormField> properties = taskFormData.getFormFields();
        Set<Magazine> magazines = this.magazineService.findMagazinesByStatus("approved");
        for (FormField element : properties) {
            if (element.getId().equals("chooseMagazineForm")) {
                for (Magazine magazine : magazines) {
                    element.getProperties().put(String.valueOf(magazine.getId()), magazine.getName());

                }
            }
        }
        return new FormFieldsDto(task.getId(), pi.getId(), properties);
    }

    @PostMapping(path = "/postChoosingMagazineForm/{taskId}/{processId}",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    ResponseEntity<?> postDataInputForm(@RequestBody List<FormSubmissionDto> dto, @PathVariable String taskId,
                                        @PathVariable String processId) {

        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskId(taskId).processInstanceId(processId).singleResult();

        //Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstaceId = processId;
        runtimeService.setVariable(processInstaceId, "choosedMagazine", dto);


        String magazineId = "";
        for (FormSubmissionDto formField : dto) {
            if (formField.getFieldId().equals("choseMagazineForm")) {
                magazineId = formField.getFieldValue();
                System.out.println("Casopis: " + magazineId);
            }
        }

        Magazine magazine = new Magazine();
        try {
            magazine = this.magazineService.findOneById(Long.parseLong(magazineId));
        } catch (NullPointerException e) {
            System.out.println("Ne moze da pronadje casopis!");
        }
        if (magazine.getName().equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        runtimeService.setVariable(processInstaceId, "openAccess", magazine.isOpenAccess());
        runtimeService.setVariable(processInstaceId, "magazineId", magazine.getId());


        formService.submitTaskForm(task.getId(), map);

        MessageResponeDTO responeDTO = new MessageResponeDTO();

//        if(! proveri ima li clanarinu){
//            ako nema
//            responeDTO.setMessage("payMembership");
//        }
        responeDTO.setMessage("ok");

        return new ResponseEntity<>(responeDTO, HttpStatus.ACCEPTED);

    }

    @GetMapping(path = "/getFormForWorkDetails/{processId}",
            produces = "application/json")
    @PreAuthorize("hasAuthority('author')")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getFormForWorksDetails(@PathVariable String processId) {


        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Author author = this.authorService.findOneByUsername(username);
        } catch (Exception e) {
            System.out.println("NIKO NIJE ULOGOVAN");
        }


        Task task = taskService.createTaskQuery().active().taskDefinitionKey("InputDetailsOfWork").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        Long magazineId = (Long) runtimeService.getVariable(processId, "magazineId");
        Magazine magazine = new Magazine();
        try {
            magazine = this.magazineService.findOneById((magazineId));
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje casopis");
        }

        List<ScienceArea> scienceAreas = magazine.getScienceAreas();
        System.out.println("Ovaj ima" + magazine.getScienceAreas().size() + "ssad" + scienceAreas.size());
        List<FormField> properties = taskFormData.getFormFields();
        for (FormField element : properties) {
            if (element.getId().equals("scienceAreaForm")) {
                for (ScienceArea scienceArea : magazine.getScienceAreas()) {
                    element.getProperties().put(String.valueOf(scienceArea.getId()), scienceArea.getName());

                }
            }
        }
        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @PostMapping(path = "/postFormForWorkingDetails/{taskId}/{processId}",
            produces = "application/json")
    @PreAuthorize("hasAuthority('author')")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    ResponseEntity<?> postFormForWorkingDetails(@RequestBody List<FormSubmissionDto> dto, @PathVariable String taskId,
                                                @PathVariable String processId) {

        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskId(taskId).processInstanceId(processId).singleResult();

        //Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstaceId = processId;
        runtimeService.setVariable(processInstaceId, "workDetails", dto);
        //runtimeService.setVariable(processInstaceId, "pdfFile", pdfFile);


        long numberOfCoAuthors = 0;
        for (FormSubmissionDto formField : dto) {
            if (formField.getFieldId().equals("numberCoauthorsForm")) {
                numberOfCoAuthors = Long.parseLong(formField.getFieldValue());
                System.out.println("Casopis: " + numberOfCoAuthors);
            }
        }


        runtimeService.setVariable(processInstaceId, "numberCoauthorsForm", numberOfCoAuthors);
        runtimeService.setVariable(processInstaceId, "taskPDF", taskId);


        // formService.submitTaskForm(task.getId(), map);

        MessageResponeDTO responeDTO = new MessageResponeDTO();
        responeDTO.setMessage("ok");

        return new ResponseEntity<>(responeDTO, HttpStatus.ACCEPTED);

    }

    @PostMapping(path = "/postPdfFile/{processId}")
    @SuppressWarnings("Duplicates")
    @PreAuthorize("hasAuthority('author')")
    public @ResponseBody
    ResponseEntity<?> postPdfFile(@PathVariable String processId, @RequestParam("pdfWork") MultipartFile file) {
        //System.out.println("Casopis: "+numberOfCoAuthors);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String fileBasePath = "C:/Users/Mirko/Desktop/MasterProjekat/proposalWorks/" + username;
        File directory = new File(fileBasePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        Path path = Paths.get(fileBasePath + "/" + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(fileName)
                .toUriString();

        String pdfFilePath = fileBasePath + "/" + fileName;
        System.out.println("PDF=" + pdfFilePath);
        runtimeService.setVariable(processId, "proposalPdfFilePath", pdfFilePath);

        List<FormSubmissionDto> dto = (List<FormSubmissionDto>) runtimeService.getVariable(processId, "workDetails");
        HashMap<String, Object> map = Utils.mapListToDto(dto);
        String taskId = (String) runtimeService.getVariable(processId, "taskPDF");
        formService.submitTaskForm(taskId, map);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/getCoAuthorsForm/{processId}",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody
    FormFieldsDto getCoAuthorsForm(@PathVariable String processId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Author author = this.authorService.findOneByUsername(username);
        } catch (Exception e) {
            System.out.println("NIKO NIJE ULOGOVAN");
        }

        Task task = taskService.createTaskQuery().active().taskDefinitionKey("AddingCoauthors").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormField> properties = taskFormData.getFormFields();

        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @PostMapping(path = "/postCoauthorForm/{taskId}/{processId}",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    @PreAuthorize("hasAuthority('author')")
    public @ResponseBody
    ResponseEntity<?> postCoauthorForm(@RequestBody List<FormSubmissionDto> dto, @PathVariable String taskId,
                                       @PathVariable String processId) {
        System.out.println("Usao u post coauthora");
        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskId(taskId).processInstanceId(processId).singleResult();

        //Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstaceId = processId;
        //runtimeService.setVariable(processInstaceId, "pdfFile", pdfFile);
        CoAuthor coAuthor = new CoAuthor();


        for (int i = 0; i < dto.size(); i++) {
            if (dto.get(i).getFieldId().equals("nameCoForm")) {
                coAuthor.setName(dto.get(i).getFieldValue());
            } else if (dto.get(i).getFieldId().equals("surnameCoForm")) {
                coAuthor.setSurname(dto.get(i).getFieldValue());
            } else if (dto.get(i).getFieldId().equals("cityCoForm")) {
                coAuthor.setCity(dto.get(i).getFieldValue());
            } else if (dto.get(i).getFieldId().equals("countryCoForm")) {
                coAuthor.setCountry(dto.get(i).getFieldValue());
            } else if (dto.get(i).getFieldId().equals("emailCoForm")) {
                coAuthor.setEmail(dto.get(i).getFieldValue());
            }
        }

        Long workId = (Long) runtimeService.getVariable(processId, "workId");
        Work work = new Work();

        try {
            work = this.workService.findById(workId);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da nadje rad");
        }

        work.getCoAuthors().add(coAuthor);
        coAuthor.setWork(work);
        this.workService.saveWork(work);

        MessageResponeDTO responeDTO = new MessageResponeDTO();
        responeDTO.setMessage("ok");
        formService.submitTaskForm(taskId, map);


        return new ResponseEntity<>(responeDTO, HttpStatus.ACCEPTED);
    }

    @GetMapping(path="/getWorksForPdfFormatFix",
    produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody ResponseEntity getWorksForPdfFix(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Author author = new Author();
        try {
            author=authorService.findOneByUsername(username);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde autora!");
        }

        List<Work> retWorks = new ArrayList<>();

        try {
            retWorks = this.workService.findByAuthorAndStatus(author, WorkStatus.fix);
        } catch (NullPointerException e) {
            System.out.println("Ne moze da najde radove!");
        }

        List<ArrivedWorkDTO> retVal = new ArrayList<>();
        for (Work work : retWorks) {
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

    @GetMapping(path="/getWorkForPdfFormatFix/{processId}",
            produces = "application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody FormFieldsDto getWorkForPdfFix(@PathVariable String processId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Author author = this.authorService.findOneByUsername(username);
        } catch (Exception e) {
            System.out.println("NIKO NIJE ULOGOVAN");
        }

        Task task = taskService.createTaskQuery().active().taskDefinitionKey("fixingPDFFormat").processInstanceId(processId).singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormField> properties = taskFormData.getFormFields();

        return new FormFieldsDto(task.getId(), processId, properties);
    }

    @PostMapping(path = "/sendFixedPdfFormatFile/{processId}/{taskId}")
    @SuppressWarnings("Duplicates")
    @PreAuthorize("hasAuthority('author')")
    public @ResponseBody
    ResponseEntity<?> sendFixedPdfFile(@PathVariable String taskId, @PathVariable String processId, @RequestParam("pdfWork") MultipartFile file) {
        //System.out.println("Casopis: "+numberOfCoAuthors);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String fileBasePath = "C:/Users/Mirko/Desktop/MasterProjekat/proposalWorks/" + username;
        File directory = new File(fileBasePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        Path path = Paths.get(fileBasePath + "/" + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(fileName)
                .toUriString();

        String pdfFilePath = fileBasePath + "/" + fileName;
        System.out.println("PDF=" + pdfFilePath);
        runtimeService.setVariable(processId, "proposalPdfFilePath", pdfFilePath);

        List<FormSubmissionDto> dto = (List<FormSubmissionDto>) runtimeService.getVariable(processId, "workDetails");
        HashMap<String, Object> map = Utils.mapListToDto(dto);
        formService.submitTaskForm(taskId, map);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}