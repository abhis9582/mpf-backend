package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.configs.dtos.StateDto;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface StateService {

    Response addUpdate(StateDto stateDto);
    void deleteState(int id);
    List<StateDto> getAll();
}
