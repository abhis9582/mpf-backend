package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.entities.Zone;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.LocalityRepository;
import com.mypropertyfact.estate.repositories.ProjectTypeRepository;
import com.mypropertyfact.estate.repositories.ZoneRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelUploadService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private FileUtils fileUtils;

    @Transactional
    public Response uploadCityZoneLocalityExcel(MultipartFile file, Integer defaultProjectTypeId) {
        Response response = new Response();
        
        if (file == null || file.isEmpty()) {
            response.setIsSuccess(0);
            response.setMessage("File is required");
            return response;
        }

        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            response.setIsSuccess(0);
            response.setMessage("Only Excel files (.xlsx or .xls) are allowed");
            return response;
        }

        // Get default ProjectType - use provided ID or fetch first available
        Optional<ProjectTypes> defaultProjectType;
        if (defaultProjectTypeId != null) {
            defaultProjectType = projectTypeRepository.findById(defaultProjectTypeId);
            if (defaultProjectType.isEmpty()) {
                response.setIsSuccess(0);
                response.setMessage("Project Type with ID " + defaultProjectTypeId + " not found. Please provide a valid Project Type ID.");
                return response;
            }
        } else {
            // If no ID provided, get the first available ProjectType
            List<ProjectTypes> projectTypes = projectTypeRepository.findAll();
            if (projectTypes.isEmpty()) {
                response.setIsSuccess(0);
                response.setMessage("No Project Types found in database. Please create a Project Type first or provide a defaultProjectTypeId parameter.");
                return response;
            }
            defaultProjectType = Optional.of(projectTypes.get(0));
        }

        int citiesCreated = 0;
        int zonesCreated = 0;
        int localitiesCreated = 0;
        int citiesUpdated = 0;
        int zonesUpdated = 0;
        int localitiesUpdated = 0;
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                response.setIsSuccess(0);
                response.setMessage("Excel file is empty or has no data rows");
                return response;
            }

            // Skip header row (row 0)
            Map<String, City> cityCache = new HashMap<>();
            Map<String, Zone> zoneCache = new HashMap<>();
            Map<String, Locality> localityCache = new HashMap<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String cityName = getCellValueAsString(row.getCell(0)); // Column A - City
                    String zoneName = getCellValueAsString(row.getCell(1)); // Column B - Zone
                    String localityName = getCellValueAsString(row.getCell(2)); // Column C - Locality

                    // Validate required fields
                    if (cityName == null || cityName.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": City name is required");
                        continue;
                    }
                    if (zoneName == null || zoneName.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Zone name is required");
                        continue;
                    }
                    if (localityName == null || localityName.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Locality name is required");
                        continue;
                    }

                    cityName = cityName.trim();
                    zoneName = zoneName.trim();
                    localityName = localityName.trim();

                    // Find or create City
                    String cityKey = cityName.toLowerCase();
                    City city = cityCache.get(cityKey);
                    if (city == null) {
                        Optional<City> existingCity = cityRepository.findByNameIgnoreCase(cityName);
                        if (existingCity.isPresent()) {
                            city = existingCity.get();
                            citiesUpdated++;
                        } else {
                            city = new City();
                            city.setName(cityName);
                            city.setSlugUrl(fileUtils.generateSlug(cityName));
                            city = cityRepository.save(city);
                            citiesCreated++;
                        }
                        cityCache.put(cityKey, city);
                    }

                    // Find or create Zone
                    String zoneKey = city.getId() + "_" + zoneName.toLowerCase();
                    Zone zone = zoneCache.get(zoneKey);
                    if (zone == null) {
                        Optional<Zone> existingZone = zoneRepository.findByZoneNameAndCityId(zoneName, city.getId());
                        if (existingZone.isPresent()) {
                            zone = existingZone.get();
                            zonesUpdated++;
                        } else {
                            zone = new Zone();
                            zone.setZoneName(zoneName);
                            zone.setSlug(fileUtils.generateSlug(zoneName));
                            zone.setCity(city);
                            zone.setActive(true);
                            zone = zoneRepository.save(zone);
                            zonesCreated++;
                        }
                        zoneCache.put(zoneKey, zone);
                    }

                    // Find or create Locality
                    String localityKey = city.getId() + "_" + zone.getId() + "_" + localityName.toLowerCase();
                    Locality locality = localityCache.get(localityKey);
                    if (locality == null) {
                        // Check if locality already exists
                        Optional<Locality> existingLocality = localityRepository
                                .findByLocalityNameAndCityIdAndZoneId(localityName, city.getId(), zone.getId());

                        if (existingLocality.isPresent()) {
                            locality = existingLocality.get();
                            localitiesUpdated++;
                        } else {
                            // Generate unique slug
                            String localitySlug = fileUtils.generateSlug(localityName);
                            String finalSlug = localitySlug;
                            int counter = 1;
                            while (localityRepository.findBySlug(finalSlug).isPresent()) {
                                finalSlug = localitySlug + "-" + counter;
                                counter++;
                            }

                            locality = new Locality();
                            locality.setLocalityName(localityName);
                            locality.setSlug(finalSlug);
                            locality.setCity(city);
                            locality.setZone(zone);
                            locality.setProjectTypes(defaultProjectType.get());
                            locality.setActive(true);
                            locality = localityRepository.save(locality);
                            localitiesCreated++;
                        }
                        localityCache.put(localityKey, locality);
                    }

                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }

            // Build success message
            StringBuilder message = new StringBuilder();
            message.append("Upload completed successfully. ");
            message.append("Cities: ").append(citiesCreated).append(" created, ").append(citiesUpdated).append(" updated. ");
            message.append("Zones: ").append(zonesCreated).append(" created, ").append(zonesUpdated).append(" updated. ");
            message.append("Localities: ").append(localitiesCreated).append(" created, ").append(localitiesUpdated).append(" updated.");

            if (!errors.isEmpty()) {
                message.append(" Errors: ").append(errors.size()).append(" row(s) had issues.");
            }

            response.setIsSuccess(1);
            response.setMessage(message.toString());
            if (!errors.isEmpty()) {
                response.setMessage(response.getMessage() + " Error details: " + String.join("; ", errors));
            }

        } catch (Exception e) {
            response.setIsSuccess(0);
            response.setMessage("Error processing Excel file: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string without decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
