package com.project.naucnacentrala.serviceIMP;

import java.util.List;

import com.project.naucnacentrala.model.ScienceArea;
import com.project.naucnacentrala.repository.ScienceAreaRepository;
import com.project.naucnacentrala.service.ScienceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScienceAreaServiceImp implements ScienceAreaService {

	@Autowired
	ScienceAreaRepository scienceAreaRepository;

	@Override
	public List<ScienceArea> findAll(){
		return scienceAreaRepository.findAll();
	}
	
	public ScienceArea findOneById(long id) {
		return scienceAreaRepository.findOneById(id);
	}
}
