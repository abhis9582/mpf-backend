package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.OwnerTypeDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;

import java.util.List;

public interface MasterOwnerTypeService {
    List<OwnerTypeDto> getAllMasterOwnerType();
    SuccessResponse addUpdateMasterOwnerType(OwnerTypeDto ownerTypeDto);
    SuccessResponse deleteMasterOwnerType(int id);
}
