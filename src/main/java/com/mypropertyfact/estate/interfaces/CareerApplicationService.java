package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.CareerApplicationDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface CareerApplicationService {
    Response submitApplication(CareerApplicationDto careerApplicationDto);
    List<CareerApplicationDto> getAllCareerApplication();
    Response deleteCareerApplication(Long id);
}
