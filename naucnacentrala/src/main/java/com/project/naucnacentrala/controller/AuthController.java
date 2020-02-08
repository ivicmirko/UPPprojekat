package com.project.naucnacentrala.controller;

import com.project.naucnacentrala.common.Utils;
import com.project.naucnacentrala.dto.LoginRequestDTO;
import com.project.naucnacentrala.dto.ProfileDTO;
import com.project.naucnacentrala.jwt.JwtTokenProvider;
import com.project.naucnacentrala.model.FormFieldsDto;
import com.project.naucnacentrala.model.FormSubmissionDto;
import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.model.SystemUser;
import com.project.naucnacentrala.service.ScienceAreaService;
import com.project.naucnacentrala.service.SystemUserService;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

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
    private ScienceAreaService scienceAreaService;


    @PostMapping(value="/login",
            produces= MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginUser, Device device){

        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword());
        Authentication authentication=authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SystemUser systemUser=(SystemUser) authentication.getPrincipal();

        if(systemUser==null){
            System.out.println("Ne moze da nadje korisnika!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String jwt=jwtTokenProvider.generateToken(systemUser.getUsername(), device);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        ProfileDTO profileDTO=new ProfileDTO(authentication.getName(), jwt, authorities);

        return new ResponseEntity<>(profileDTO,HttpStatus.OK);

    }

    @GetMapping("/user/logout")
    public void logout(){
        System.out.println("LOGOUT");
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    @GetMapping("/getLogged")
    public ResponseEntity<?> getLogged(HttpServletRequest request){

        SystemUser user=this.systemUserService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if(user !=null){

            List<String> authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String jwt=request.getHeader("Authorization").substring(7);
            ProfileDTO profile=new ProfileDTO(user.getUsername(),jwt, authorities);

            return new ResponseEntity<ProfileDTO>(profile, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Fail",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path="/getDataInputForm",
            produces="application/json")
    @SuppressWarnings("Duplicates")
    public @ResponseBody FormFieldsDto getDataInputForm() {

        ProcessInstance pi=runtimeService.startProcessInstanceByKey("registration");
        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);


        TaskFormData taskFormData=formService.getTaskFormData(task.getId());

        List<FormField> properties=taskFormData.getFormFields();
        List<ScienceArea> scienceAreas=this.scienceAreaService.findAll();
        for(FormField element : properties) {
            if(element.getId().equals("formScienceArea")) {
                for(ScienceArea sa:scienceAreas) {
                    element.getProperties().put(String.valueOf(sa.getId()), sa.getName());

                }
            }
        }

        return new FormFieldsDto(task.getId(),pi.getId(),properties);

    }

    @PostMapping(path="/postDataInputForm/{taskId}/{processId}",
            produces="application/json")
    public @ResponseBody ResponseEntity<?> postDataInputForm(@RequestBody List<FormSubmissionDto> dto, @PathVariable String taskId,
                                                             @PathVariable String processId){
        HashMap<String, Object> map = Utils.mapListToDto(dto);
        Task task = taskService.createTaskQuery().active().taskName("DataInput").processInstanceId(processId).singleResult();

        //Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstaceId=processId;
        runtimeService.setVariable(processInstaceId, "registration", dto);


        String email="";
        String username="";
        String reviewer="false";
        for(FormSubmissionDto formField:dto) {
            if(formField.getFieldId().equals("formEmail")) {
                email=formField.getFieldValue();
            }

            if(formField.getFieldId().equals("formUsername")) {
                username=formField.getFieldValue();
            }

            if(formField.getFieldId().equals("formReviewer")) {
                if(formField.getFieldValue().equals("true") || formField.getFieldValue().equals(true)) {
                    System.out.println("Zeli biti reviewer");
                    reviewer="true";
                }
            }
        }


        runtimeService.setVariable(processInstaceId, "confirmed", false);
        runtimeService.setVariable(processInstaceId, "valid", true);
        runtimeService.setVariable(processInstaceId, "reviewer", reviewer);

        int check=0;
        check=this.systemUserService.checkCredentials(username,email);

        formService.submitTaskForm(task.getId(), map);

        if(check != 0){
            runtimeService.setVariable(processInstaceId, "valid", false);
            return new ResponseEntity<>("Neispravni email ili username",HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }

    @GetMapping(path="/activating/{username}/{processId}",
            produces="application/json")
    public @ResponseBody ResponseEntity<?> activateUser(@PathVariable String username,@PathVariable String processId){

        SystemUser user=systemUserService.findByUsername(username);
        System.out.println("Pronasao usera"+user.getUsername());
        runtimeService.setVariable(processId, "confirmed", true);
        //user.setActive(true);
        runtimeService.setVariable(processId, "revName", user.getName());
        runtimeService.setVariable(processId, "revSurname", user.getSurname());
        //systemUserService.saveSystemUser(user);
        Task task = taskService.createTaskQuery().active().taskName("ConfirmingRegistration").processInstanceId(processId).singleResult();
        runtimeService.setVariable(processId, "username", username);

        taskService.complete(task.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
