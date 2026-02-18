package com.mypropertyfact.estate;

import com.mypropertyfact.estate.dtos.ProjectShortDetails;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class ProjectTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void getAllProjects() {
        long start = System.currentTimeMillis();
        log.info("Project fetching started at: {}", start);
        List<ProjectShortDetails> allProjectedBy = projectRepository.findAllProjects();
        log.info("Total projects are: {}", allProjectedBy.size());
        long end = System.currentTimeMillis();
        log.info("Project fetching completed at : {}", end);
        log.info("Total time consumed in fetching all projects are: {}", end-start);
    }
}
