package com.project.naucnacentrala.newWorkHandlers;

import com.project.naucnacentrala.service.WorkService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexingWorkHandler implements JavaDelegate {

    @Autowired
    WorkService workService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("USAO U INDEKSIRANJE RADA");
    }
}
