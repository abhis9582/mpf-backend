package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.FirmTypeDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.MasterFirmType;

import java.util.List;

public interface MasterFirmTypeService {
    List<MasterFirmType> getAllMasterFirmType();
    SuccessResponse addUpdateFirmType(FirmTypeDto firmTypeDto);
    SuccessResponse deleteMasterFirmType(int id);
}
