package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.CountryDto;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;

import java.util.List;
import java.util.Map;

public interface CountryService {
    Response addUpdate(CountryDto countryDto);
    Response deleteCountry(int id);
    List<CountryDto> getAllCountry();
}
