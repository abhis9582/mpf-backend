package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.SuccessResponse;
import com.mypropertyfact.estate.entities.Enquery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.EnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enquiry")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Enquery>> getAll(){
        return new ResponseEntity<>(enquiryService.getAll(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdate(@RequestBody Enquery enquery){
        return new ResponseEntity<>(enquiryService.addUpdate(enquery), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteEnquiry(@PathVariable int id){
        return new ResponseEntity<>(enquiryService.deleteEnquiry(id), HttpStatus.OK);
    }

    @PutMapping("/update-status/{enquiryId}")
    public ResponseEntity<SuccessResponse> updateStatus(@PathVariable("enquiryId") int enquiryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(enquiryService.updateStatus(enquiryId, request.get("status")));
    }

    @GetMapping("/by-property/{propertyId}")
    public ResponseEntity<List<Enquery>> getByPropertyId(@PathVariable Long propertyId){
        return new ResponseEntity<>(enquiryService.getByPropertyId(propertyId), HttpStatus.OK);
    }

    @GetMapping("/get-user-leads")
    public ResponseEntity<?> getUserLeads(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        if(enquiryService.getUserLeads(email) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(0, "Invalid token", 0));
        }
        return new ResponseEntity<>(enquiryService.getUserLeads(email), HttpStatus.OK);
    }
}
