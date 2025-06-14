package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.CountryService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Override
    public Response addUpdate(Country country) {
        if (country.getId() > 0) {
            Optional<Country> savedCountry = countryRepository.findById(country.getId());
            savedCountry.ifPresent(dbCountry -> {
                dbCountry.setCountryName(country.getCountryName());
                dbCountry.setContinent(country.getContinent());
                dbCountry.setDescription(country.getDescription());
                countryRepository.save(dbCountry);

            });
            return new Response(1, "Country updated successfully...");
        }
        countryRepository.save(country);
        return new Response(1, "Country saved successfully...");
    }

    @Override
    public void deleteCountry(int id) {

    }

    @Override
    public List<Map<String, Object>> getAll() {
        List<City> cityList = cityRepository.findAll();

        Map<Integer, Map<String, Object>> response = new HashMap<>();

        for (City city : cityList) {
            if (city.getState() != null) {
                int stateId = city.getState().getId();
                Map<String, Object> countryEntity = new HashMap<>();
                if (city.getState().getCountry() != null) {
                    int countryId = city.getState().getCountry().getId();
                    countryEntity = response.computeIfAbsent(countryId, id -> {
                        Map<String, Object> country = new HashMap<>();
                        country.put("countryId", id);
                        if (city.getState().getCountry() != null) {
                            country.put("countryName", city.getState().getCountry().getCountryName());
                            country.put("countryDesc", city.getState().getCountry().getDescription());
                        }
                        country.put("states", new HashMap<Integer, Map<String, Object>>());
                        return country;
                    });
                }

                Map<Integer, Map<String, Object>> stateMap = (Map<Integer, Map<String, Object>>) countryEntity.get("states");

                Map<String, Object> stateEntity = stateMap.computeIfAbsent(stateId, id -> {
                    Map<String, Object> state = new HashMap<>();
                    state.put("stateId", city.getState().getId());
                    state.put("stateName", city.getState().getStateName());
                    state.put("stateDesc", city.getState().getDescription());
                    state.put("cities", new ArrayList<>());
                    return state;
                });

                List<Map<String, Object>> cityListObj = (List<Map<String, Object>>) stateEntity.get("cities");

                Map<String, Object> cityObj = new HashMap<>();
                cityObj.put("id", city.getId());
                cityObj.put("cityName", city.getName());
                cityListObj.add(cityObj);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> country : response.values()) {
            Map<Integer, Map<String, Object>> stateMap = (Map<Integer, Map<String, Object>>) country.remove("states");
            country.put("states", new ArrayList<>(stateMap.values()));
            result.add(country);
        }
        return result;
    }

    @Override
    public List<Country> getAllCountry() {
        return List.of();
    }
}
