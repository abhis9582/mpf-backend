package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.FloorPlanDto;
import com.mypropertyfact.estate.entities.FloorPlan;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.FloorPlanRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FloorPlanService {
    @Autowired
    private FloorPlanRepository floorPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllPlans() {
        // Efficient native query that only selects the columns we need
        // Returns: [floorPlanId, planType, areaSqft, areaSqMt, projectId, projectName]
        List<Object[]> results = floorPlanRepository.findAllFloorPlansWithProjectInfo();
        
        if (results.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Use LinkedHashMap to preserve insertion order, pre-size based on expected projects
        Map<Integer, ProjectData> projectDataMap = new LinkedHashMap<>(results.size() / 4); // Rough estimate: 4 plans per project
        
        // Single pass grouping - optimized for performance
        for (Object[] row : results) {
            int projectId = ((Number) row[4]).intValue();
            
            // Get or create project data structure
            ProjectData projectData = projectDataMap.computeIfAbsent(projectId, id -> {
                String name = (String) row[5];
                return new ProjectData(id, name != null ? name : "");
            });
            
            // Build plan map directly
            if(row[0] != null) {
            int floorPlanId = ((Number) row[0]).intValue();
                String planType = (String) row[1];
                double areaSqft = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
                double areaSqMt = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
                
                projectData.addPlan(floorPlanId, planType, areaSqft, areaSqMt);
            }
        }
        
        // Convert to final result list and sort by project name (more efficient than DB ORDER BY)
        List<Map<String, Object>> result = new ArrayList<>(projectDataMap.size());
        for (ProjectData data : projectDataMap.values()) {
            result.add(data.toMap());
        }
        
        // Sort by project name after grouping (faster than sorting all rows in DB)
        result.sort((a, b) -> {
            String nameA = (String) a.get("projectName");
            String nameB = (String) b.get("projectName");
            if (nameA == null) nameA = "";
            if (nameB == null) nameB = "";
            return nameA.compareToIgnoreCase(nameB);
        });
        
        return result;
    }
    
    // Helper class to reduce object creation overhead
    private static class ProjectData {
        private final int projectId;
        private final String projectName;
        private final List<Map<String, Object>> plans;
        
        ProjectData(int projectId, String projectName) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.plans = new ArrayList<>(); // Will grow as needed
        }
        
        void addPlan(int id, String planType, double areaSqft, double areaSqMt) {
            Map<String, Object> plan = new HashMap<>(4); // Pre-size for 4 keys
            plan.put("id", id);
            plan.put("planType", planType != null ? planType : "");
            plan.put("areaSqft", areaSqft);
            plan.put("areaSqMt", areaSqMt);
            plans.add(plan);
        }
        
        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>(3); // Pre-size for 3 keys
            map.put("projectId", projectId);
            map.put("projectName", projectName);
            map.put("plans", plans);
            return map;
        }
    }

    public Response addUpdatePlan(FloorPlanDto floorPlan) {
        Response response = new Response();
        try {
            if (floorPlan == null || floorPlan.getPlanType().isEmpty()) {
                response.setMessage("Plan type is required");
                return response;
            }
            double areaSqMt = floorPlan.getAreaSqFt() * 0.092903;
            floorPlan.setAreaSqMt(areaSqMt);
            Optional<Project> projectById = this.projectRepository.findById(floorPlan.getProjectId());
            if (floorPlan.getFloorId() > 0) {
                Optional<FloorPlan> plan = this.floorPlanRepository.findById(floorPlan.getFloorId());
                if (plan.isPresent()) {
                    FloorPlan dbFloorPlan = plan.get();
                    dbFloorPlan.setPlanType(floorPlan.getPlanType());
                    dbFloorPlan.setAreaSqft(floorPlan.getAreaSqFt());
                    dbFloorPlan.setAreaSqmt(floorPlan.getAreaSqMt());
                    projectById.ifPresent(dbFloorPlan::setProject);
                    this.floorPlanRepository.save(dbFloorPlan);
                    response.setMessage("Floor Plan Updated Successfully...");
                    response.setIsSuccess(1);
                }
            } else {
                FloorPlan floorPlanObj = new FloorPlan();
                floorPlanObj.setPlanType(floorPlan.getPlanType());
                floorPlanObj.setAreaSqmt(floorPlan.getAreaSqMt());
                floorPlanObj.setAreaSqft(floorPlan.getAreaSqFt());
                projectById.ifPresent(floorPlanObj::setProject);
                this.floorPlanRepository.save(floorPlanObj);
                response.setMessage("Floor Plan Saved Successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteFloorPlan(int id) {
        try {
            this.floorPlanRepository.deleteById(id);
            return new Response(1, "Deleted successfully...", 0);
        } catch (Exception e) {
            return new Response(0, e.getMessage(), 0);
        }
    }
}
