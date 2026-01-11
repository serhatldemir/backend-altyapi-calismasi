package com.staj.backend_gorev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staj.backend_gorev.BaseIntegrationTest;
import com.staj.backend_gorev.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser_FullIntegration() throws Exception {
        // 1. Hazırlık: Test verisi oluşturma
        UserDTO requestDto = new UserDTO();
        requestDto.setName("Mehmet");
        requestDto.setSurname("Demir");

        // 2. İstek Atma: API'ye POST isteği gönderiyoruz
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))

                // 3. Kontrol: Gerçek veritabanından dönen sonuçları doğrulama
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mehmet"))
                .andExpect(jsonPath("$.surname").value("Demir"))
                .andExpect(jsonPath("$.id").exists()); // ID artık DB tarafından üretiliyor
    }
}