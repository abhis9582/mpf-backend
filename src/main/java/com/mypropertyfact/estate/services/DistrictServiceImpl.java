package com.mypropertyfact.estate.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypropertyfact.estate.dtos.DistrictDto;
import com.mypropertyfact.estate.dtos.IndianData;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.District;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.DistrictService;
import com.mypropertyfact.estate.repositories.DistrictRepository;
import com.mypropertyfact.estate.repositories.StateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final ObjectMapper objectMapper;
    private final StateRepository stateRepository;

    @Override
    public SuccessResponse addAllDetailsFromFile(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {

            // Parse full JSON as a tree
            JsonNode root = objectMapper.readTree(inputStream);

            // Extract only "records" array
            JsonNode recordsNode = root.get("records");

            // Convert to List<IndianData>
            List<IndianData> dataList = objectMapper.convertValue(
                    recordsNode,
                    new TypeReference<List<IndianData>>() {}
            );

            // Save to DB
            for (IndianData data : dataList) {
                // indianDataRepository.save(data);
                District district = new District();
                Optional<District> districtByName = districtRepository.findByName(data.getDistrict());
                Optional<State> state = stateRepository.findByStateName(data.getStateName());
                if(districtByName.isEmpty()){
                    district.setName(data.getDistrict());
                    state.ifPresent(district::setState);
                    districtRepository.save(district);
                }
                log.info("Saving: {} - {}", data.getOfficeName(), data.getPinCode());
            }
            return new SuccessResponse(1,"Uploaded " + dataList.size() + " records successfully");

        } catch (Exception e) {
            log.error(e.getMessage());
            return new SuccessResponse(0, "Error: " + e.getMessage());
        }
    }


    @Override
    public SuccessResponse addUpdateDistrict(DistrictDto districtDto) {
        return null;
    }

    @Override
    public List<DistrictDto> getAllDistrict() {
        districtRepository.findAll();
        return List.of();
    }

    @Override
    public SuccessResponse deleteDistrict(int id) {
        districtRepository.deleteById(id);
        return new SuccessResponse(1, "District Deleted Successfully");
    }
}
