package com.mypropertyfact.estate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.services.AmenityService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class AmenitiesTests {

    @Autowired
    private AmenityService amenityService;

    @Test
    public void testGetAmenitiesByProjectId() {
        int projectId = 1072;
        List<Amenity> amenities = amenityService.getAmenitiesByProjectId(projectId);
        log.info("Amenities: {}", amenities);
        assertNotNull(amenities);
    }
}
