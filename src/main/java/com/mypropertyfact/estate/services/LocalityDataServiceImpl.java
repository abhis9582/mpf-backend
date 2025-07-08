package com.mypropertyfact.estate.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypropertyfact.estate.configs.dtos.LocalityDataResponse;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.LocalityData;
import com.mypropertyfact.estate.interfaces.LocalityDataService;
import com.mypropertyfact.estate.repositories.LocalityDataRepository;
import com.mypropertyfact.estate.repositories.LocalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocalityDataServiceImpl implements LocalityDataService {

    @Autowired
    private LocalityDataRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalityRepository localityRepository;

    @Override
    public LocalityData saveLocalityData(LocalityData localityData) {
        return repository.save(localityData);
    }

    @Override
    public LocalityDataResponse getByLocalityId(String localityId) throws JsonProcessingException {
        LocalityData entity = repository.findByLocalityId(localityId)
                .orElseThrow(() -> new RuntimeException("Locality not found"));

        LocalityDataResponse dto = new LocalityDataResponse();

        dto.setLocalityId(entity.getLocalityId());
        dto.setLocalEconomyList(objectMapper.readValue(entity.getLocalEconomyList(), new TypeReference<List<String>>() {}));
        dto.setLocalEconomyScore(entity.getLocalEconomyScore());

        dto.setOnGoingFutureProjectsList(objectMapper.readValue(entity.getOnGoingFutureProjectsList(), new TypeReference<List<String>>() {}));
        dto.setOnGoingFutureProjectsScore(entity.getOnGoingFutureProjectsScore());

        dto.setConnectivityAndCommuteList(objectMapper.readValue(entity.getConnectivityAndCommuteList(), new TypeReference<List<String>>() {}));
        dto.setConnectivityAndCommuteScore(entity.getConnectivityAndCommuteScore());

        dto.setAmenitiesAndGentrificationList(objectMapper.readValue(entity.getAmenitiesAndGentrificationList(), new TypeReference<List<String>>() {}));
        dto.setAmenitiesAndGentrificationScore(entity.getAmenitiesAndGentrificationScore());

        dto.setTrendsAndHostoricalDataList(objectMapper.readValue(entity.getTrendsAndHistoricalDataList(), new TypeReference<List<String>>() {}));
        dto.setTrendsAndHistoricalDataScore(entity.getTrendsAndHistoricalDataScore());

        dto.setExestingSupplyList(objectMapper.readValue(entity.getExistingSupplyList(), new TypeReference<List<String>>() {}));
        dto.setExistingSupplyScore(entity.getExistingSupplyScore());

        dto.setInterpretationAndOutlookList(objectMapper.readValue(entity.getInterpretationAndOutlookList(), new TypeReference<List<String>>() {}));
        dto.setRecommendationsForInvestorsList(objectMapper.readValue(entity.getRecommendationsForInvestorsList(), new TypeReference<List<String>>() {}));

        return dto;
    }

    @Override
    public List<LocalityDataResponse> getAllLocalities() throws JsonProcessingException {
        List<LocalityData> entities = repository.findAll();
        List<LocalityDataResponse> responses = new ArrayList<>();

        for (LocalityData entity : entities) {
            LocalityDataResponse dto = new LocalityDataResponse();
            Optional<Locality> localityById = localityRepository.findById(Long.parseLong(entity.getLocalityId()));
            dto.setLocalityId(entity.getLocalityId());
            dto.setId(entity.getId());
            localityById.ifPresent(locality -> {
                dto.setLocalityName(locality.getLocalityName());
            });
            dto.setLocalEconomyList(objectMapper.readValue(entity.getLocalEconomyList(), new TypeReference<List<String>>() {}));
            dto.setLocalEconomyScore(entity.getLocalEconomyScore());

            dto.setOnGoingFutureProjectsList(objectMapper.readValue(entity.getOnGoingFutureProjectsList(), new TypeReference<List<String>>() {}));
            dto.setOnGoingFutureProjectsScore(entity.getOnGoingFutureProjectsScore());

            dto.setConnectivityAndCommuteList(objectMapper.readValue(entity.getConnectivityAndCommuteList(), new TypeReference<List<String>>() {}));
            dto.setConnectivityAndCommuteScore(entity.getConnectivityAndCommuteScore());

            dto.setAmenitiesAndGentrificationList(objectMapper.readValue(entity.getAmenitiesAndGentrificationList(), new TypeReference<List<String>>() {}));
            dto.setAmenitiesAndGentrificationScore(entity.getAmenitiesAndGentrificationScore());

            dto.setTrendsAndHostoricalDataList(objectMapper.readValue(entity.getTrendsAndHistoricalDataList(), new TypeReference<List<String>>() {}));
            dto.setTrendsAndHistoricalDataScore(entity.getTrendsAndHistoricalDataScore());

            dto.setExestingSupplyList(objectMapper.readValue(entity.getExistingSupplyList(), new TypeReference<List<String>>() {}));
            dto.setExistingSupplyScore(entity.getExistingSupplyScore());

            dto.setInterpretationAndOutlookList(objectMapper.readValue(entity.getInterpretationAndOutlookList(), new TypeReference<List<String>>() {}));
            dto.setRecommendationsForInvestorsList(objectMapper.readValue(entity.getRecommendationsForInvestorsList(), new TypeReference<List<String>>() {}));

            responses.add(dto);
        }

        return responses;
    }
}
