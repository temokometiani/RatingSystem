package com.ratingsystem.integration;

import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.GameObjectRepository;
import com.ratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GameObjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("ratingsystem")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void setDatasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void initData() {
        gameObjectRepository.deleteAll();
        userRepository.deleteAll();

        seller = User.builder()
                .firstName("temo")
                .lastName("kometiani")
                .email("kometiani@mail.com")
                .password("temo123")
                .role(Role.SELLER)
                .approved(true)
                .emailConfirmed(true)
                .createdAt(LocalDateTime.now())
                .build();

        seller = userRepository.save(seller);

        assertThat(seller).isNotNull();
        assertThat(seller.getId()).isNotNull();

        GameObject obj = GameObject.builder()
                .title("NAB25")
                .text("shooting style")
                .user(seller)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        gameObjectRepository.save(obj);
    }

    @Test
    void testGetAllGameObjects() throws Exception {
        mockMvc.perform(get("/api/objects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("NAB25"))
                .andExpect(jsonPath("$[0].text").value("shooting style"))
                .andExpect(jsonPath("$[0].userId").value(seller.getId()))
                .andExpect(jsonPath("$[0].userName").value("temo kometiani"));
    }

    //tested
    /*
    A seller is created
    A game object is created and assigned to that seller
    When hitting /api/objects, the system returns a list of game objects
    The JSON contains correct:title,text,sellerId,seller full name
    Database interaction works
    Repository works
    DTO serialization works
    Request → controller → service → repository → database → DTO → JSON works end-to-end
     */
}
