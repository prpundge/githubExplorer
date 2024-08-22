package com.github.explorer.controller;

import com.github.explorer.exception.RepositoryNotFoundException;
import com.github.explorer.module.Github;
import com.github.explorer.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class GithubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubRepositoryService githubRepositoryService;

    @Test
    void testGetRepositoryDetails_RepositoryFound() throws Exception {
        // Arrange
        String owner = "testowner";
        String repositoryName = "testrepo";
        Github repository = new Github();
        repository.setFullName(owner + "/" + repositoryName);

        when(githubRepositoryService.getRepositoryDetails(owner, repositoryName)).thenReturn(repository);

        // Act
        MockHttpServletResponse response = mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andReturn()
                .getResponse();

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(owner, repositoryName);
        verify(githubRepositoryService, times(1)).getRepositoryDetails(owner, repositoryName);
    }

    @Test
    void testGetRepositoryDetails_RepositoryNotFound() throws Exception {
        // Arrange
        String owner = "testowner";
        String repositoryName = "testrepo";

        when(githubRepositoryService.getRepositoryDetails(owner, repositoryName)).thenThrow(new RepositoryNotFoundException(owner, repositoryName));

        // Act
        MockHttpServletResponse response = mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andReturn()
                .getResponse();

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(githubRepositoryService, times(1)).getRepositoryDetails(owner, repositoryName);
    }
}