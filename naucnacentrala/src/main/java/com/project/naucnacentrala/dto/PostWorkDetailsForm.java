package com.project.naucnacentrala.dto;

import com.project.naucnacentrala.model.FormSubmissionDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PostWorkDetailsForm {

    private List<FormSubmissionDto> dataForm;
    private MultipartFile pdfWorkForma;

    public PostWorkDetailsForm(){

    }

    public List<FormSubmissionDto> getDataForm() {
        return dataForm;
    }

    public void setDataForm(List<FormSubmissionDto> dataForm) {
        this.dataForm = dataForm;
    }

    public MultipartFile getPdfWorkForma() {
        return pdfWorkForma;
    }

    public void setPdfWorkForma(MultipartFile pdfWorkForma) {
        this.pdfWorkForma = pdfWorkForma;
    }
}
