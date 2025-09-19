package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.MasterBrokerDTO;
import com.mypropertyfact.estate.entities.MasterBroker;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface MasterBrokerService {
    List<MasterBroker> getAllMasterBrokerService();
    Response addMasterBroker(MasterBrokerDTO masterBrokerDto);
    Response deleteMasterBroker(int id);
}
