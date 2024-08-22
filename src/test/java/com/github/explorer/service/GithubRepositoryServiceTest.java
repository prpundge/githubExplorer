package com.github.explorer.service;

import com.github.explorer.exception.RepositoryNotFoundException;
import com.github.explorer.module.Github;
import com.github.explorer.repo.GithubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {
    @Mock
    private GithubRepository githubRepository;

    @InjectMocks
    private GithubRepositoryService githubRepositoryService;

    @Spy
    private GithubRepositoryService githubRepositoryServiceSpyy;


    @Mock
    private RestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        githubRepositoryService.setRestTemplate(restTemplate);
    }

    @Test
    void testGetsRepositoryDetails_RepositoryFound() {
        String owner = "octocat";
        String repositoryName = "Hello-World";
        String repositoryId = owner + "/" + repositoryName;

        Github expectedRepository = new Github();
        expectedRepository.setFullName(repositoryId);

        when(githubRepository.findById(repositoryId)).thenReturn(Optional.of(expectedRepository));

        Github actualRepository = githubRepositoryService.getRepositoryDetails(owner, repositoryName);

        assertThat(actualRepository).isEqualTo(expectedRepository);
        verify(githubRepository).findById(repositoryId);
        verifyNoMoreInteractions(githubRepository);
    }


    @Test
    void testGetRepositoryDetails_IllegalArgument() {
        String owner = null;
        String repositoryName = null;

        Throwable thrown = catchThrowable(() -> githubRepositoryService.getRepositoryDetails(owner, repositoryName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Owner and repository name must not be null or empty");
        verifyNoInteractions(githubRepository);
    }


    @Test
    void testGetRepositoryDetails_RepositoryFound() {
        String owner = "prpundge";
        String repositoryName = "testRepository";
        String repositoryId = owner + "/" + repositoryName;

        Github expectedRepository = new Github();
        expectedRepository.setId(repositoryId);

        when(githubRepository.findById(repositoryId)).thenReturn(Optional.of(expectedRepository));

        Github actualRepository = githubRepositoryService.getRepositoryDetails(owner, repositoryName);

        assertThat(actualRepository).isEqualTo(expectedRepository);
        verify(githubRepository, times(1)).findById(repositoryId);
        verify(githubRepository, times(0)).save(any(Github.class));
    }

    @Test
    void testGetRepositoryDetails_RepositoryNotFound() {
        String owner = "prpundge";
        String repositoryName = "testRepository";
        String repositoryId = owner + "/" + repositoryName;

        when(githubRepository.findById(repositoryId)).thenReturn(Optional.empty());
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class), any(Map.class)))
                .thenReturn(new ResponseEntity<>(new Github(), HttpStatus.OK));

        Github actualRepository = githubRepositoryService.getRepositoryDetails(owner, repositoryName);

        assertThat(actualRepository).isNotNull();
        verify(githubRepository, times(1)).findById(repositoryId);
        verify(githubRepository, times(1)).save(any(Github.class));
    }

    @Test
    void testGetRepositoryDetails_RepositoryNotFoundInGithubApi() {
        String owner = "prpundge";
        String repositoryName = "Angular_Practice";
        String repositoryId = owner + "/" + repositoryName;

        when(githubRepository.findById(repositoryId)).thenReturn(Optional.empty());
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class), any(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> githubRepositoryService.getRepositoryDetails(owner, repositoryName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Repository 'prpundge/Angular_Practice' not found");

        verify(githubRepository, times(1)).findById(repositoryId);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(Class.class), any(Map.class));
    }

    @Test
    void testGetRepositoryDetails_ErrorFetchingFromGithubApi() {
        String owner = "prpundge";
        String repositoryName = "testRepository";
        String repositoryId = owner + "/" + repositoryName;

        when(githubRepository.findById(repositoryId)).thenReturn(Optional.empty());
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class), any(Map.class)))
                .thenThrow(new RestClientException("Error fetching repository details"));

        assertThatThrownBy(() -> githubRepositoryService.getRepositoryDetails(owner, repositoryName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Repository 'prpundge/testRepository' not found");

        verify(githubRepository, times(1)).findById(repositoryId);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(Class.class), any(Map.class));
    }
}