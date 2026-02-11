package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.MasterBenefitDto;
import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.interfaces.MasterBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/benefit")
public class MasterBenefitController {

    @Autowired
    private MasterBenefitService masterBenefitService;

    @GetMapping
    public ResponseEntity<?> getAllBenefits() {
        return ResponseEntity.ok(masterBenefitService.getAllBenefits());
    }

    @PostMapping
    public ResponseEntity<?> addUpdateBenefit(@RequestParam("file") MultipartFile file, @ModelAttribute MasterBenefitDto masterBenefitDto) {
        return ResponseEntity.ok(masterBenefitService.addUpdateBenefit(file, masterBenefitDto));
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse> uploadLocationBenefits(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(masterBenefitService.postBulkBenefits(files));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBenefit(@PathVariable("id")int id) {
        return ResponseEntity.ok(masterBenefitService.deleteBenefit(id));
    }
}
