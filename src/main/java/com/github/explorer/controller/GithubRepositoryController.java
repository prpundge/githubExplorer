package com.github.explorer.controller;


import com.github.explorer.module.Github;
import com.github.explorer.service.GithubRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class GithubRepositoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubRepositoryController.class);
    @Autowired
    private GithubRepositoryService githubRepositoryService;

    /**
     * Retrieves the details of a GitHub repository.
     *
     * @param owner          the owner of the repository
     * @param repositoryName the name of the repository
     * @return a ResponseEntity containing the repository details, or a 404 status if not found
     */
    @GetMapping("/{owner}/{repositoryName}")
    public ResponseEntity<Github> getRepositoryDetails(
            @PathVariable String owner, @PathVariable String repositoryName) {
        // Log the incoming request
        LOGGER.info("Getting repository details for {}/{}", owner, repositoryName);

        Github repository = githubRepositoryService.getRepositoryDetails(owner, repositoryName);

        if (repository != null) {
            // Log the successful retrieval
            LOGGER.info("Repository found: {}", repository.getFullName());
            return ResponseEntity.ok(repository);
        } else {
            // Log the not found error
            LOGGER.warn("Repository not found: {}/{}", owner, repositoryName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}