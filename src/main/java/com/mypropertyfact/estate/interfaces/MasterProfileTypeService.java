package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.ProfileTypeDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.MasterProfileType;

import java.util.List;

public interface MasterProfileTypeService {

    List<MasterProfileType> getAllMasterProfileType();
    SuccessResponse addUpdateMasterProfileType(ProfileTypeDto profileTypeDto);
    SuccessResponse deleteMasterProfileType(int id);
}
