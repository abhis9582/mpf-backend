package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LocationBenefitDto;
import com.mypropertyfact.estate.entities.LocationBenefit;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.LocationBenefitRepository;
import com.mypropertyfact.estate.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationBenefitService {
    @Autowired
    private LocationBenefitRepository locationBenefitRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Value("${upload_icon_path}")
    private String uploadDir;
    public List<LocationBenefitDto> getAllBenefits(){
        List<Object[]> response = this.locationBenefitRepository.getAllWithProjectName();
        return response.stream().map(item-> new LocationBenefitDto(
                (int) item[0],
                (String) item[1],
                (String) item[2],
                (String) item[3],
                (String) item[4]
        )).collect(Collectors.toList());
    }

    public Response addUpdateBenefit(LocationBenefitDto locationBenefitDto){
        Response response = new Response();
        try{
            if(locationBenefitDto == null || locationBenefitDto.getIconImage().isEmpty()){
                response.setMessage("All fields are required !");
                return response;
            }
            if(!locationBenefitDto.getIconImage().getContentType().startsWith("image")){
                response.setMessage("Only image is allowed !");
                return response;
            }
            if(locationBenefitDto.getIconImage().getSize() > 10 * 1024 * 1024){
                response.setMessage("Image should be < 10MB !");
                return response;
            }
            int dotIndex = locationBenefitDto.getIconImage().getOriginalFilename().lastIndexOf(".");
            String extension = (dotIndex > 0) ? locationBenefitDto.getIconImage().getOriginalFilename().substring(dotIndex) : "";
            String iconImageName = UUID.randomUUID().toString() + extension;
            File destinationDir = new File(uploadDir);
            if(!destinationDir.exists()){
                destinationDir.mkdirs();
            }
            if(locationBenefitDto.getProjectId() > 0){
                Property property = this.propertyRepository.findById(locationBenefitDto.getProjectId()).get();
                locationBenefitDto.setSlugUrl(property.getSlugURL());
            }
            File destinationFile = new File(destinationDir, iconImageName);
            locationBenefitDto.getIconImage().transferTo(destinationFile);
            LocationBenefit locationBenefit = new LocationBenefit();
            locationBenefit.setBenefitName(locationBenefitDto.getBenefitName());
            locationBenefit.setDistance(locationBenefitDto.getDistance());
            locationBenefit.setSlugUrl(locationBenefitDto.getSlugUrl());
            locationBenefit.setIconImage(iconImageName);
            locationBenefit.setProjectId(locationBenefitDto.getProjectId());
            this.locationBenefitRepository.save(locationBenefit);
            response.setMessage("Location benefit added successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<LocationBenefit> getBySlug(String url) {
        return this.locationBenefitRepository.findBySlugUrl(url);
    }
}
