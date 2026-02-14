package com.mypropertyfact.estate.services;
import com.mypropertyfact.estate.dtos.ProjectFaqDto;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.ProjectFaqsRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectFaqsService {

    private final ProjectFaqsRepository projectFaqsRepository;

    private final ProjectRepository projectRepository;

    public List<Map<String, Object>> getAllFaqs() {
        List<ProjectFaqs> projectFaqList = this.projectFaqsRepository.findAll();
        Map<Integer, Map<String, Object>> resultObj = new HashMap<>();
        for (ProjectFaqs projectFaqs: projectFaqList){
            int projectId= projectFaqs.getProject().getId();
            Map<String, Object> projectFaq = resultObj.computeIfAbsent(projectId, id-> {
                Map<String, Object> projectObject = new HashMap<>();
                projectObject.put("projectId", id);
                projectObject.put("projectName", projectFaqs.getProject().getProjectName());
                projectObject.put("projectFaq", new ArrayList<Map<String, Object>>());
                return projectObject;
            });

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> faqObject = (List<Map<String, Object>>) projectFaq.get("projectFaq");

            Map<String, Object> obj= new HashMap<>();
            obj.put("id", projectFaqs.getId());
            obj.put("question", projectFaqs.getFaqQuestion());
            obj.put("answer", projectFaqs.getFaqAnswer());
            faqObject.add(obj);
        }

        return new ArrayList<>(resultObj.values());
    }

    public Response addUpdateFaqs(ProjectFaqDto projectFaqDto) {
        Response response = new Response();
        try {
            if (projectFaqDto == null || projectFaqDto.getQuestion().isEmpty() || projectFaqDto.getAnswer().isEmpty()) {
                response.setMessage("All fields are required !");
                return response;
            }
            Optional<Project> project = projectRepository.findById(projectFaqDto.getProjectId());

            if (projectFaqDto.getId() > 0) {
                Optional<ProjectFaqs> savedFaqs = projectFaqsRepository.findById(projectFaqDto.getId());
                savedFaqs.ifPresent(faqs -> {
                            faqs.setFaqQuestion(projectFaqDto.getQuestion());
                            faqs.setFaqAnswer(projectFaqDto.getAnswer());
                            project.ifPresent(faqs::setProject);
                            projectFaqsRepository.save(faqs);
                            response.setMessage("Faqs updated successfully...");
                            response.setIsSuccess(1);
                        }
                );
            } else {
                ProjectFaqs projectFaqs = new ProjectFaqs();
                project.ifPresent(projectFaqs::setProject);
                projectFaqs.setFaqQuestion(projectFaqDto.getQuestion());
                projectFaqs.setFaqAnswer(projectFaqDto.getAnswer());
                this.projectFaqsRepository.save(projectFaqs);
                response.setIsSuccess(1);
                response.setMessage("Faqs added successfully...");
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<ProjectFaqs> getBySlug(String url) {
        List<ProjectFaqs> response;
        try {
            response = this.projectFaqsRepository.findBySlugUrl(url);
        } catch (Exception e) {
            response = new ArrayList<>();
        }
        return response;
    }

    public Response deleteFaq(int id) {
        Response response = new Response();
        try {
            Optional<ProjectFaqs> byId = projectFaqsRepository.findById(id);
            if (byId.isPresent()) {
                projectFaqsRepository.deleteById(id);
                response.setMessage("FAQ deleted successfully...");
                response.setIsSuccess(1);
            } else {
                response.setMessage("FAQ already deleted or not exists");
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
