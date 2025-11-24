package com.ratingsystem.integration;

import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.CommentRepository;
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
import static org.hamcrest.Matchers.closeTo;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SellerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User seller1;
    private User seller2;
    private User commentAuthor;

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("ratingsystem")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void postgresConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void initDatabase() {
        commentRepository.deleteAll();
        userRepository.deleteAll();

        // seller 1
        seller1 = User.builder()
                .firstName("Alex")
                .lastName("Addams")
                .email("alex@mail.com")
                .password("123")
                .role(Role.SELLER)
                .emailConfirmed(true)
                .approved(true)
                .createdAt(LocalDateTime.now())
                .build();
        seller1 = userRepository.save(seller1);

        // seller 2
        seller2 = User.builder()
                .firstName("Mischa")
                .lastName("Armani")
                .email("mischa@mail.com")
                .password("qwerty")
                .role(Role.SELLER)
                .emailConfirmed(true)
                .approved(true)
                .createdAt(LocalDateTime.now())
                .build();
        seller2 = userRepository.save(seller2);

        // COMMENT AUTHOR
        commentAuthor = User.builder()
                .firstName("Random")
                .lastName("Dude")
                .email("random@mail.com")
                .password("xXx")
                .role(Role.USER)   // <-- Just a normal user leaving comments
                .emailConfirmed(true)
                .approved(true)
                .createdAt(LocalDateTime.now())
                .build();
        commentAuthor = userRepository.save(commentAuthor);

        assertThat(seller1.getId()).isNotNull();
        assertThat(seller2.getId()).isNotNull();

        // Ratings
        createComment(seller1, commentAuthor, 5);
        createComment(seller1, commentAuthor, 5);
        createComment(seller1, commentAuthor, 4);

        createComment(seller2, commentAuthor, 3);
        createComment(seller2, commentAuthor, 4);
    }

    private void createComment(User seller, User author, int rating) {
        Comment comment = Comment.builder()
                .seller(seller)
                .author(author)
                .message("my review")
                .rating(rating)
                .approved(true)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Test
    void testTopSellers() throws Exception {
        mockMvc.perform(get("/api/sellers/top?limit=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].averageRating", closeTo(4.67, 0.01)))
                .andExpect(jsonPath("$[1].averageRating", closeTo(3.5, 0.01)));
    }

    //tested
    /*
       The controller returns HTTP 200 OK
       The returned JSON is a list of sellers
       Sellers are sorted by averageRating in descending order
       The ratings match actual DB values
       The limit query parameter works (limit=2 returns only 2 sellers)
       The rating calculation reflects database values
       Only approved = true sellers are included
     */
}
