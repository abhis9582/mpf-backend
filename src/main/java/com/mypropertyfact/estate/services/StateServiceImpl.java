package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.StateDto;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.StateService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;
import com.mypropertyfact.estate.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StateServiceImpl implements StateService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Response addUpdate(StateDto stateDto) {
        Optional<Country> countryById = countryRepository.findById(stateDto.getCountryId());
        if(stateDto.getId() > 0){
            Optional<State> savedState = stateRepository.findById(stateDto.getId());
            savedState.ifPresent(state-> {
                state.setStateName(stateDto.getStateName());
                state.setDescription(stateDto.getDescription());
                countryById.ifPresent(state::setCountry);
                stateRepository.save(state);
            });
            return new Response(1, "State updated successfully...");
        }
        State state= new State();
        state.setStateName(stateDto.getStateName());
        state.setDescription(stateDto.getDescription());
        countryById.ifPresent(state::setCountry);
        stateRepository.save(state);
        return new Response(1, "State saved successfully...");
    }

    @Override
    public void deleteState(int id) {

    }

    @Override
    public List<StateDto> getAll() {
        List<State> stateList = stateRepository.findAll();
        return stateList.stream()
                .filter(Objects::nonNull).map(state-> {
            StateDto stateDto = new StateDto();
            stateDto.setId(state.getId());
            stateDto.setStateName(state.getStateName());
            stateDto.setDescription(state.getDescription());
            if(state.getCountry() != null) {
                stateDto.setCountryId(state.getCountry().getId());
                stateDto.setCountryName(state.getCountry().getCountryName());
            }
            return stateDto;
        }).toList();
    }
}
