package com.github.explorer;

import com.github.explorer.exception.RepositoryNotFoundException;
import com.github.explorer.module.Github;
import com.github.explorer.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
class GithubRepositoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GithubRepositoryService githubRepositoryService;

    @Test
    void testGetRepositoryDetails_RepositoryFound() throws Exception {
        String owner = "octocat";
        String repositoryName = "Hello-World";

        MvcResult result = mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).contains(owner, repositoryName);

        Github repository = githubRepositoryService.getRepositoryDetails(owner, repositoryName);
        assertThat(repository).isNotNull();
        assertThat(repository.getFullName()).isEqualTo(owner + "/" + repositoryName);
    }

    @Test
    void testGetRepositoryDetails_RepositoryNotFound() throws Exception {
        String owner = "nonexistent-owner";
        String repositoryName = "nonexistent-repo";

        MvcResult result = mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(result.getResolvedException()).isInstanceOf(RepositoryNotFoundException.class);}
}