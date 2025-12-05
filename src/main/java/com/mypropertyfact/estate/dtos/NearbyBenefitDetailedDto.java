package com.mypropertyfact.estate.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class NearbyBenefitDetailedDto {

    private Integer id;
    private String benefitName;
    private String benefitAltTag;
    private String benefitImage;
    private List<MultipartFile> nearbyBenefitsFiles;
    private List<Integer> deletedNearbyBenefitsIds;

}
