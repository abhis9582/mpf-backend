package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.ProjectStatusDto;
import com.mypropertyfact.estate.interfaces.ProjectStatusService;
import com.mypropertyfact.estate.models.Response;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-status")
public class ProjectStatusController {

    private final ProjectStatusService projectStatusService;

    public ProjectStatusController(ProjectStatusService projectStatusService){
        this.projectStatusService = projectStatusService;
    }

    // Get all project statuses
    @GetMapping
    public ResponseEntity<List<ProjectStatusDto>> getAllProjectStatus() {
        return ResponseEntity.ok(projectStatusService.getAllStatus());
    }

    // Add or update project status
    @PostMapping
    public ResponseEntity<?> addUpdateProjectStatus(@RequestBody @Valid ProjectStatusDto projectStatusDto) {
        Response response = projectStatusService.addUpdate(projectStatusDto);
        if(response.getIsSuccess() == 1){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
