package com.project.naucnacentrala.newWorkHandlers;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

@Service
public class CreatingChoosingMagazine implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("USAO U KREIRANJE CHOOSING MAGAZINE TASK");
    }
}
