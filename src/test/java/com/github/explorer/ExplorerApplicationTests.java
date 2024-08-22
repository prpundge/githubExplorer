package com.github.explorer;

import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExplorerApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

}
