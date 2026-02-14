package com.mypropertyfact.estate.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mypropertyfact.estate.dtos.LocalityDataResponse;
import com.mypropertyfact.estate.entities.LocalityData;

import java.util.List;

public interface LocalityDataService {
    LocalityData saveLocalityData(LocalityData localityData);
    LocalityDataResponse getByLocalityId(String localityId) throws JsonProcessingException;
    List<LocalityDataResponse> getAllLocalities() throws JsonProcessingException;
}
