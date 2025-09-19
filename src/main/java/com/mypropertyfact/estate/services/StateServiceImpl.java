package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.CommonMapper;
import com.mypropertyfact.estate.dtos.CityDto;
import com.mypropertyfact.estate.dtos.StateDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.District;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.StateService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;
import com.mypropertyfact.estate.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public Response addUpdate(StateDto stateDto) {
        Optional<Country> countryById = countryRepository.findById(stateDto.getCountryId());
        if (stateDto.getId() > 0) {
            Optional<State> savedState = stateRepository.findById(stateDto.getId());
            savedState.ifPresent(state -> {
                state.setStateName(stateDto.getStateName());
                state.setDescription(stateDto.getStateDescription());
                countryById.ifPresent(state::setCountry);
                stateRepository.save(state);
            });
            return new Response(1, "State updated successfully...", 0);
        }
        State state = new State();
        state.setStateName(stateDto.getStateName());
        state.setDescription(stateDto.getStateDescription());
        countryById.ifPresent(state::setCountry);
        stateRepository.save(state);
        return new Response(1, "State saved successfully...", 0);
    }

    @Override
    public void deleteState(int id) {
        stateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<StateDto> getAll() {
        List<State> stateList = stateRepository.findAll();
        List<StateDto> stateDtoList;
        stateDtoList = stateList.stream()
                .filter(Objects::nonNull).map(state -> {
                    StateDto stateDto = new StateDto();
                    stateDto.setId(state.getId());
                    stateDto.setStateName(state.getStateName());
                    stateDto.setStateDescription(state.getDescription());
                    if (state.getDistricts() != null) {
                        List<CityDto> cityDtoList = state.getDistricts().stream()
                                .filter(district -> district.getCities() != null) // filter out null cities
                                .flatMap(district -> district.getCities().stream()) // flatten all cities
                                .sorted(Comparator.comparing(City::getName, String.CASE_INSENSITIVE_ORDER)) // sort by name
                                .map(city -> {
                                    CityDto cityDto = new CityDto();
                                    commonMapper.mapCityDtoToCity(cityDto, city);
                                    return cityDto;
                                })
                                .toList();

                        stateDto.setCityList(cityDtoList); // finally set the cities
                    }
                    if (state.getCountry() != null) {
                        Country country = state.getCountry();
                        stateDto.setCountryId(country.getId());
                        stateDto.setCountryName(country.getCountryName());
                    }
                    return stateDto;
                }).toList();
        return stateDtoList;
    }
}
