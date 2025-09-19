package com.mypropertyfact.estate.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypropertyfact.estate.dtos.DistrictDto;
import com.mypropertyfact.estate.dtos.IndianData;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.District;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.DistrictService;
import com.mypropertyfact.estate.repositories.CountryRepository;
import com.mypropertyfact.estate.repositories.DistrictRepository;
import com.mypropertyfact.estate.repositories.StateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final ObjectMapper objectMapper;
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    DistrictServiceImpl(DistrictRepository districtRepository, ObjectMapper objectMapper,
                        StateRepository stateRepository, CountryRepository countryRepository){
        this.districtRepository = districtRepository;
        this.objectMapper = objectMapper;
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

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
                City city = new City();
                District district = new District();
                Optional<District> districtByName = districtRepository.findByName(data.getDistrict());
                Optional<State> state = stateRepository.findByStateName(data.getStateName());
                if(!districtByName.isPresent()){
                    district.setName(data.getDistrict());
                    state.ifPresent(district::setState);
                    districtRepository.save(district);
                }
//                System.out.println("Saving: " + data.getOfficeName() + " - " + data.getPinCode());
            }
            return new SuccessResponse(1,"Uploaded " + dataList.size() + " records successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessResponse(0, "Error: " + e.getMessage());
        }
    }


    @Override
    public SuccessResponse addUpdateDistrict(DistrictDto districtDto) {
        if(districtDto.getId() > 0){

        }else{

        }
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
