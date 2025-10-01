package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.MasterBenefitDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MasterBenefitService {
    SuccessResponse addUpdateBenefit(MultipartFile file, MasterBenefitDto masterBenefitDto);
    SuccessResponse deleteBenefit(int id);
    List<MasterBenefitDto> getAllBenefits();
    SuccessResponse postBulkBenefits(List<MultipartFile> files);
}
