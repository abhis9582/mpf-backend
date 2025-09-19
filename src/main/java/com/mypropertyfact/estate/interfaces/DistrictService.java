package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.DistrictDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DistrictService {
    SuccessResponse addUpdateDistrict(DistrictDto districtDto);
    List<DistrictDto> getAllDistrict();
    SuccessResponse deleteDistrict(int id);
    SuccessResponse addAllDetailsFromFile(MultipartFile multipartFile);
}
