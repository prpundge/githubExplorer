package com.github.explorer.service;

import com.github.explorer.controller.GithubRepositoryController;
import com.github.explorer.exception.RepositoryNotFoundException;
import com.github.explorer.module.Github;
import com.github.explorer.repo.GithubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GithubRepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubRepositoryService.class);

    // Define the GitHub API endpoint URL as a constant
    private static final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repositoryName}";

    @Autowired
    private GithubRepository githubRepository;

    private RestTemplate restTemplate = new RestTemplate();


    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the details of a GitHub repository.
     *
     * @param owner          the owner of the repository
     * @param repositoryName the name of the repository
     * @return the GitHub repository details, or null if not found
     */
    public Github getRepositoryDetails(String owner, String repositoryName) {
        if (owner == null || owner.isEmpty() || repositoryName == null || repositoryName.isEmpty()) {
            throw new IllegalArgumentException("Owner and repository name must not be null or empty");
        }

        String repositoryId = String.format("%s/%s", owner, repositoryName);
        return githubRepository.findById(repositoryId)
                .orElseGet(() -> fetchAndSaveRepositoryDetails(owner, repositoryName));
    }

    private Github fetchAndSaveRepositoryDetails(String owner, String repositoryName) {
        Github github = fetchRepositoryDetailsFromGithub(owner, repositoryName);
        if (github != null) {
            github.setId(String.format("%s/%s", owner, repositoryName));
            githubRepository.save(github);
        }
        return github;
    }

    /**
     * Fetches the details of a GitHub repository from the GitHub API.
     *
     * @param owner          the owner of the repository
     * @param repositoryName the name of the repository
     * @return the GitHub repository details
     */
    Github fetchRepositoryDetailsFromGithub(String owner, String repositoryName) {
        LOGGER.info("Fetching repository details from GitHub API for {}/{}", owner, repositoryName);
        if (owner == null || owner.isEmpty() || repositoryName == null || repositoryName.isEmpty()) {
            throw new IllegalArgumentException("Owner and repository name must not be null or empty");
        }
        try {
            // Use the RestTemplate to fetch the repository details from the GitHub API
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("owner", owner);
            uriVariables.put("repositoryName", repositoryName);

            ResponseEntity<Github> response = restTemplate.exchange(
                    GITHUB_API_URL, HttpMethod.GET, null, Github.class, uriVariables
            );
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                LOGGER.error("Failed to fetch repository details from GitHub API for {}/{}", owner, repositoryName);
                throw new RuntimeException("Failed to fetch repository details");
            }
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.error("Repository not found on GitHub API for {}/{}", owner, repositoryName);
            throw new RepositoryNotFoundException(owner, repositoryName);
        } catch (RestClientException e) {
            LOGGER.error("Error fetching repository details from GitHub API for {}/{}", owner, repositoryName, e);
            throw new RuntimeException(String.format("Repository '%s/%s' not found", owner, repositoryName));
        }
    }
}