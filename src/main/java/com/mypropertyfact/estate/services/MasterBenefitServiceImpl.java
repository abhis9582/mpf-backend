package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.dtos.MasterBenefitDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.MasterBenefit;
import com.mypropertyfact.estate.interfaces.MasterBenefitService;
import com.mypropertyfact.estate.repositories.MasterBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterBenefitServiceImpl implements MasterBenefitService {

    private final MasterBenefitRepository masterBenefitRepository;

    private final FileUtils fileUtils;

    @Value("${upload_dir}")
    private String uploadDir;

    @Override
    public SuccessResponse addUpdateBenefit(MultipartFile file, MasterBenefitDto masterBenefitDto) {
        String benefitIcon = "";
        String filePath = uploadDir.concat( "location-benefit/");
        try {
            if (file != null) {
                if (!fileUtils.isTypeImage(file)) {
                    throw new IllegalArgumentException("File should be type of image only");
                }
                if(!fileUtils.isFileSizeValid(file, 5 * 1024 * 1024)){
                    throw new IllegalArgumentException("File size should not be more than 5MB");
                }
                benefitIcon = fileUtils.saveDesktopImageWithResize(file, filePath, 100, 100, 0.85f);
            }
            if (masterBenefitDto.getId() > 0) {
                Optional<MasterBenefit> masterBenefit = masterBenefitRepository.findById(masterBenefitDto.getId());
                if(masterBenefit.isPresent()) {
                    MasterBenefit benefit = masterBenefit.get();
                    benefit.setBenefitIcon(benefitIcon);
                    benefit.setBenefitName(masterBenefitDto.getBenefitName());
                    masterBenefitRepository.save(benefit);
                    return new SuccessResponse(1, "Benefit updated successfully...");
                }
            } else {
                MasterBenefit masterBenefit = new MasterBenefit();
                masterBenefit.setBenefitName(masterBenefitDto.getBenefitName());
                masterBenefit.setBenefitIcon(benefitIcon);
                masterBenefitRepository.save(masterBenefit);
                return new SuccessResponse(1, "Benefit added successfully...");
            }
        }catch (Exception e) {
            if(!benefitIcon.isBlank()){
                fileUtils.deleteFileFromDestination(benefitIcon, filePath);
            }
            return new SuccessResponse(0, e.getMessage());
        }
        return null;
    }



    @Override
    public SuccessResponse deleteBenefit(int id) {
        masterBenefitRepository.deleteById(id);
        return new SuccessResponse(1, "Location benefit deleted successfully...");
    }

    @Override
    public List<MasterBenefitDto> getAllBenefits() {
        List<MasterBenefit> benefits = masterBenefitRepository.findAll();
        return benefits.stream().map(benefit-> {
            MasterBenefitDto dto = new MasterBenefitDto();
            dto.setId(benefit.getId());
            dto.setBenefitIcon(benefit.getBenefitIcon());
            dto.setBenefitName(benefit.getBenefitName());
            dto.setAltTag(benefit.getAltTag());
            return dto;
        }).toList();
    }

    @Transactional
    @Override
    public SuccessResponse postBulkBenefits(List<MultipartFile> files) {
        List<String> savedImages = new ArrayList<>();
        String iconPath = uploadDir.concat("location-benefit/");
        try {
            for (MultipartFile file : files) {
                if (!fileUtils.isTypeImage(file)) {
                    throw new IllegalArgumentException("File should be image only !");
                }
                if(!fileUtils.isFileSizeValid(file, 2*1024*1024)) {
                    throw new IllegalArgumentException("File size should not be more than 2MB !");
                }
                String iconName = fileUtils.generateNameFromImage(file);
                Optional<MasterBenefit> byBenefitName = masterBenefitRepository.findByBenefitName(iconName);
                if(byBenefitName.isPresent()){
                    continue;
                }
                String savedIcon = fileUtils.saveDesktopImageWithResize(file, iconPath, 100, 100, 0.85f);
                savedImages.add(savedIcon);
                MasterBenefit benefit = new MasterBenefit();
                benefit.setBenefitName(iconName);
                benefit.setAltTag(fileUtils.generateImageAltTag(file));
                benefit.setBenefitIcon(savedIcon);
                masterBenefitRepository.save(benefit);
            }
            return new SuccessResponse(1, "All files are uploaded successfully...");
        }catch(Exception e){
            for(String image: savedImages) {
                fileUtils.deleteFileFromDestination(image, iconPath);
            }
            return new SuccessResponse(0, e.getMessage());
        }
    }
}
