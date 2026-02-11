package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/excel-upload")
public class ExcelUploadController {

    @Autowired
    private ExcelUploadService excelUploadService;

    @PostMapping("/city-zone-locality")
    public ResponseEntity<Response> uploadCityZoneLocality(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "defaultProjectTypeId", required = false) Integer defaultProjectTypeId) {
        return ResponseEntity.ok(excelUploadService.uploadCityZoneLocalityExcel(file, defaultProjectTypeId));
    }
}
