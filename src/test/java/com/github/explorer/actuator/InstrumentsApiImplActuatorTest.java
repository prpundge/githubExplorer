package com.github.explorer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfig.class)
@WebAppConfiguration
class InstrumentsApiImplActuatorTest {

    private static final String ACTUATOR_URL = "/actuator/";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    protected void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void healthCheckShouldReportStatusUpIncludingAllComponents() throws Exception {
        mockMvc.perform(get(ACTUATOR_URL + "health"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status", is("UP"))
                );
    }
}