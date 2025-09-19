package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.RoleDto;
import com.mypropertyfact.estate.entities.MasterRole;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface MasterRoleService {
    List<MasterRole> getAllRoles();
    Response addUpdateRole(RoleDto roleDto);
    Response deleteRole(int id);
}
