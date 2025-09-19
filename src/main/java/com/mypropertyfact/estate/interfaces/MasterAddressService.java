package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.AddressDto;
import com.mypropertyfact.estate.entities.MasterAddress;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface MasterAddressService {
    List<MasterAddress> getAllMasterAddresses();
    Response addUpdateAddress(AddressDto addressDto);
    Response deleteMasterAddress(int id);
}
