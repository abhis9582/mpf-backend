package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;

import java.util.List;
import java.util.Map;

public interface CountryService {
    Response addUpdate(Country country);
    void deleteCountry(int id);
    List<Map<String, Object>> getAll();
    List<Map<String, Object>> getAllCountry();
}
