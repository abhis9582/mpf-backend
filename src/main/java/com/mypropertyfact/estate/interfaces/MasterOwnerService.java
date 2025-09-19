package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.OwnerTypeDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;

import java.util.List;

public interface MasterOwnerService {
    List<OwnerTypeDto> getAllMasterOwner();
    SuccessResponse addUpdateMasterOwner(OwnerTypeDto ownerTypeDto);
    SuccessResponse deleteMasterOwner(int id);
}
