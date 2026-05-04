package com.climbup;

import com.climbup.repository.MembershipRepository;
import com.climbup.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PublicControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository userRepo;
    @Autowired MembershipRepository membershipRepo;

    @BeforeEach
    void clean() {
        membershipRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void register_createsUserAndPendingMembership() throws Exception {
        // Membership type with id=1 is seeded by DataSeeder
        var body = Map.of(
            "name", "Alice Gym",
            "email", "alice@gym.com",
            "phone", "0501234567",
            "membershipTypeId", 1
        );

        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.status").value("PENDING"));

        assertThat(userRepo.existsByEmail("alice@gym.com")).isTrue();
        assertThat(membershipRepo.findAll()).hasSize(1);
        assertThat(membershipRepo.findAll().get(0).getStartDate()).isNull();
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        var body = Map.of(
            "name", "Alice Gym",
            "email", "alice@gym.com",
            "membershipTypeId", 1
        );
        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
            .andExpect(status().isConflict());
    }
}
