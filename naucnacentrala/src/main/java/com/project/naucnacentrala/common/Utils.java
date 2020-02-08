package com.project.naucnacentrala.common;

import com.project.naucnacentrala.model.FormSubmissionDto;

import java.util.HashMap;
import java.util.List;

public class Utils {

    public static HashMap<String, Object> mapListToDto(List<FormSubmissionDto> list) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        for(FormSubmissionDto temp : list){
            map.put(temp.getFieldId(), temp.getFieldValue());
        }

        return map;
    }
}
