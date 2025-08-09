package com.devansh.integration;

import com.devansh.model.Role;
import com.devansh.repo.CategoryRepository;
import com.devansh.repo.UserRepository;
import com.devansh.request.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("admin@gmail.com")
                .fullname("theadmin")
                .password("admin")
                .role(Role.ADMIN)
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.instanceOf(String.class)))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        accessToken = node.get("accessToken").asText();
    }

    @AfterEach
    public void tearDown() throws Exception {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create new category")
    public void createCategory() throws Exception {
        // given
        String name = "RAW";

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/category/admin")
                .param("name", name)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(name)));
    }


    @Test
    @DisplayName("Update category")
    public void updateCategory() throws Exception {
        // given
        String name = "RAW";
        MvcResult initResult = mockMvc.perform(MockMvcRequestBuilders.post("/category/admin")
                        .param("name", name)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(name)))
                .andReturn();

        String jsonResponse = initResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        int id = node.get("id").asInt();

        String newName = "COOKED";
        mockMvc.perform(MockMvcRequestBuilders.put("/category/admin/{id}", id)
                .header("Authorization", "Bearer " + accessToken)
                .param("newName", newName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(newName)));
    }

    @Test
    @DisplayName("Delete category by id")
    public void deleteCategoryById() throws Exception {
        // create category
        String name = "RAW";
        MvcResult initResult = mockMvc.perform(MockMvcRequestBuilders.post("/category/admin")
                        .param("name", name)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(name)))
                .andReturn();

        String jsonResponse = initResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        int id = node.get("id").asInt();

        System.out.println("Category id: " + id);

        // delete category
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/admin/{id}", id)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // check if category still exist or not
        mockMvc.perform(MockMvcRequestBuilders.get("/category/{id}", id)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}






















