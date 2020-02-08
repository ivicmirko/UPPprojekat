package com.project.naucnacentrala.registrationHandlers;

import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.service.ScienceAreaService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDataInputHandler implements TaskListener {

    @Autowired
    private ScienceAreaService scienceAreaService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    @Override
    public void notify(DelegateTask delegateTask) {

//        Task task= taskService.createTaskQuery().processInstanceId(delegateTask.getProcessInstanceId()).list().get(0);
//
//        TaskFormData taskFormData=formService.getTaskFormData(task.getId());
//
//        List<FormField> properties=taskFormData.getFormFields();
//        List<ScienceArea> scienceAreas=this.scienceAreaService.findAll();
//        for(FormField element : properties) {
//            if(element.getId().equals("formScienceArea")) {
//                for(ScienceArea sa:scienceAreas) {
//                    element.getProperties().put(String.valueOf(sa.getId()), sa.getName());
//
//                }
//            }
//        }

    }
}
